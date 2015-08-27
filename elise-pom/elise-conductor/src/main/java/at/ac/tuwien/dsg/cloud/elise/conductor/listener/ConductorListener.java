/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.conductor.listener;

import at.ac.tuwien.dsg.cloud.elise.collector.CollectorSettings.ConductorConfiguration;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.wrapper.UnitInstanceWrapper;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.CollectorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;

/**
 * This class listen to the request from other elise and execute the command
 *
 * @author Duc-Hung Le
 *
 */
public class ConductorListener {

    static Logger logger = ConductorConfiguration.logger;
    static Set<Class<? extends UnitInstanceCollector>> instanceCollectorClasses = new HashSet<>();
    static URLClassLoader urlClassLoader = null;
    static MessageClientFactory factory = MessageClientFactory.getFactory(ConductorConfiguration.getBroker(), ConductorConfiguration.getBrokerType());

    public static void main(String[] args) {
        logger.info("Conductor is starting. Generated ID: " + ConductorConfiguration.getConductorID());
        // register conductor
        // registerConductor();

        // loading all the collectors
        instanceCollectorClasses = loadAllJar();

        // Listening to the query
        logger.debug("Subscribing to the control topic : {}", EliseQueueTopic.QUERY_TOPIC);

        MessageSubscribeInterface subscriber = factory.getMessageSubscriber(new SalsaMessageHandling() {
            Map<String, String> answeredElises = new HashMap();

            @Override
            public void handleMessage(SalsaMessage message) {
                String fromElise = message.getFromSalsa();
                String feedbackTopic = message.getFeedbackTopic();
                logger.debug("Retrieve the request from ELISE: " + fromElise + ", feedback topic: " + feedbackTopic);
                if (feedbackTopic.equals(this.answeredElises.get(fromElise))) {
                    logger.debug("Neglect duplicated subscribing message from ELISE: " + fromElise + ", topic: " + feedbackTopic);
                    return;
                }
                this.answeredElises.put(fromElise, feedbackTopic);

                logger.debug("Listenner got a control message from ELISE of type: " + message.getMsgType());

                String response;
                SalsaMessage resMsg = null;
                switch (message.getMsgType()) {
                    case discover:
                        logger.debug("  --- Basic conductDesp start");
                        ConductorDescription conductDesp = new ConductorDescription(ConductorConfiguration.getConductorID(), ConductorConfiguration.getELISE_IP());
                        logger.debug("  --- Basic conductDesp done");
                        for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
                            try {
                                logger.debug("  --- Trying to create instance");

                                UnitInstanceCollector u = (UnitInstanceCollector) urlClassLoader.loadClass(c.getName()).newInstance();

                                logger.debug("  --- Trying to create instance DONE");
                                String allConfigStr = u.readAllAdaptorConfig();
                                logger.debug("All config string: {}", allConfigStr);
                                String[] allConfig = u.readAllAdaptorConfig().split("\\r?\\n");
                                for (String s : allConfig) {
                                    logger.debug("   ALL-CONFIG-ARRAY: {}", s);
                                }
                                logger.debug("  --- Trying to create instance and get all config DONE");
                                conductDesp.hasCollector(new CollectorDescription(u.getName(), allConfig));
                                logger.debug("When creating conductor description, we have collector: {}", u.getName());
                            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                                logger.error("Cannot create instance of class: {}", c.toString(), ex);
                            }
                        }
                        response = conductDesp.toJson();
                        logger.debug("Sending back the conductor description: {}", response);
                        resMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, ConductorConfiguration.getConductorID(), feedbackTopic, null, response);
                        break;
                    case elise_queryInstance:
                        // first send a notification to say that this ELISE is processing the info
                        MessagePublishInterface publish = factory.getMessagePublisher();
                        String queryUUID = message.getFeedbackTopic().substring(message.getFeedbackTopic().lastIndexOf(".") + 1);
                        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_queryProcessNotification, ConductorConfiguration.getConductorID(), EliseQueueTopic.NOTIFICATION_TOPIC, null,
                                new EliseQueryProcessNotification(queryUUID, message.getFromSalsa(), ConductorConfiguration.getConductorID(), EliseQueryProcessNotification.QueryProcessStatus.PROCESSING).toJson()));

                        Set<UnitInstance> instances = runAllUnitInstanceCollector(message.getPayload());
                        UnitInstanceWrapper wrapper = new UnitInstanceWrapper(instances);
                        response = wrapper.toJson();

                        resMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_instanceInfoUpdate, ConductorConfiguration.getConductorID(), feedbackTopic, null, response);
                        break;
                    case elise_queryProvider:
                        response = "{\"message\":\"Not support query provider yet\"}";
                        break;
                    case elise_instanceInfoUpdate:
                        return;
                    default:
                        response = "{\"error\":\"Unknown Elise command !\"}";
                        break;
                }
                if (resMsg != null) {
                    MessagePublishInterface publisher = factory.getMessagePublisher();
                    logger.debug("Pushing answer data...");
                    publisher.pushMessage(resMsg);
                } else {
                    logger.warn("Do not know how to reply the request, so resMsg is null !MsgType: {}", message.getMsgType());
                }
            }
        });

        subscriber.subscribe(EliseQueueTopic.QUERY_TOPIC);

        logger.info("Conductor initiation is done !");

    }

    private static void registerConductor() {
        logger.info("Registering the collector: " + ConductorConfiguration.getConductorID());
        
        
        
        MessagePublishInterface publish = factory.getMessagePublisher();
        // TODO: Later, publish more detail about the conductor, e.g. list of collector
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, ConductorConfiguration.getConductorID(), EliseQueueTopic.NOTIFICATION_TOPIC, "", "");
        publish.pushMessage(msg);
        logger.info("Registering message is published, not sure an ELISE can received it ! Note: we need to unicast, or ID checking.");
    }

    // query all instance
    private static Set<UnitInstance> runAllUnitInstanceCollector(String query) {
        logger.debug("Execute all the collectors...");
        Set<UnitInstance> unitInstances = new HashSet<>();

        for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
            try {
                UnitInstanceCollector collector = c.newInstance();
                unitInstances.addAll(collector.collectAllInstance());
                for (UnitInstance i : unitInstances) {
                    System.out.println("Adding unit instance: " + i.getName() + "/" + i.getId());
                    LocalIdentification si = collector.identify(i);
                    GlobalIdentification gi = new GlobalIdentification(si.getCategory());   // the instance contain a globalID, but null uuid and has 1 localID
                    gi.addLocalIdentification(si);
                    i.setIdentification(gi.toJson());
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                logger.error("Error when initiate the class instance for collector. Class name: {}", c.getClass().getName(), ex);
            }
        }
        return unitInstances;

//        String queryURL = EliseConfiguration.getRESTEndpointLocal() + "/unitinstance/query";
//        WebClient client = WebClient.create(queryURL);
//        logger.debug("Querying to local REST: " + queryURL);
//
//        HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
//
//        conduit.getClient().setReceiveTimeout(600000L); // maximum a collector collect data is 10 min, blocking
//        conduit.getClient().setConnectionTimeout(30000L);
//        return (String) client.accept(new String[]{"application/json"}).type("application/json").post(query, String.class);
    }

    public void sendAllData(UnitInstanceCollector aCollector) {
        logger.debug("Inside the sending data method ...");

        UnitInstanceWrapper instances = new UnitInstanceWrapper(aCollector.collectAllInstance());
        logger.debug("Prepare to send items, Number: " + instances.getUnitInstances().size());
        for (UnitInstance i : instances.getUnitInstances()) {
            System.out.println("Adding local identification for instance: " + i.getName() + "/" + i.getId());
            LocalIdentification si = aCollector.identify(i);
            GlobalIdentification gi = new GlobalIdentification(si.getCategory());   // the instance contain a globalID, but null uuid and has 1 localID
            gi.addLocalIdentification(si);
            i.setIdentification(gi.toJson());
        }
        MessagePublishInterface publish = factory.getMessagePublisher();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_instanceInfoUpdate, ConductorConfiguration.getConductorID(), EliseQueueTopic.FEEDBACK_TOPIC, "", instances.toJson());
        publish.pushMessage(msg);
    }

    public void sendSingleInstanceByID(UnitInstanceCollector aCollector, ServiceCategory category, String domainID) {
        logger.debug("Inside the sending data method ...");

        UnitInstanceWrapper wrapper = new UnitInstanceWrapper();
        UnitInstance instance = aCollector.collectInstanceByID(category, domainID);
        LocalIdentification si = aCollector.identify(instance);
        GlobalIdentification gi = new GlobalIdentification(si.getCategory());   // the instance contain a globalID, but null uuid and has 1 localID
        gi.addLocalIdentification(si);
        instance.setIdentification(gi.toJson());

        wrapper.hasInstance(instance);

        MessagePublishInterface publish = factory.getMessagePublisher();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_instanceInfoUpdate, ConductorConfiguration.getConductorID(), EliseQueueTopic.FEEDBACK_TOPIC, "", wrapper.toJson());
        publish.pushMessage(msg);

    }

    private static final String mainFolder = ConductorConfiguration.CURRENT_DIR + "/extensions";

    private static Set<Class<? extends UnitInstanceCollector>> loadAllJar() {
        logger.debug("Running all collector ...");

        String[] folders = listSubFolder(mainFolder);
        logger.debug("Number of child folders: " + folders.length);
        List<URL> allURLs = new ArrayList<>();

        for (String f : folders) {
            String checkingDir = mainFolder + "/" + f;
            logger.debug("Checking folder: " + f);
            File file = new File(checkingDir);

            String[] jars = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            logger.debug("Loading jar into classpath. Number of jar files: " + jars.length);
            for (String jar : jars) {
                String absoluteJar = checkingDir + "/" + jar;
                String urlToFile = "jar:file:" + absoluteJar + "!/";
                try {
                    logger.debug("adding url to file: " + urlToFile);
                    allURLs.add(new URL(urlToFile));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        logger.debug("Using reflections to gather all UnitInstanceCollector classes");
        urlClassLoader = new URLClassLoader(allURLs.toArray(new URL[allURLs.size()]));

        Reflections reflextions = new Reflections(new ConfigurationBuilder()
                .addClassLoader(urlClassLoader)
                .addUrls(ClasspathHelper.forClassLoader(urlClassLoader)));

        Set<Class<? extends UnitInstanceCollector>> classes = reflextions.getSubTypesOf(UnitInstanceCollector.class);
        logger.debug("Loaded {} Elise collector(s) in the ./extensions folder", classes.size());
        for (Class c : classes) {
            if (c == null) {
                logger.error("Loaded class is null, I do not know what is happening !");
            } else {
                logger.debug(c.toString());
            }
        }
        return classes;
    }

    private static String[] listSubFolder(String mainFolder) {
        File file = new File(mainFolder);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        return directories;
    }

    private static String executeCommand3(String command, String workingDir) {
        StringBuffer output = new StringBuffer();
        String[] env = {"/bin", "/usr/bin", "/opt/java/bin"};
        try {
            Process p = Runtime.getRuntime().exec(command, env, new File(workingDir));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }
            while ((line = reader1.readLine()) != null) {
                logger.debug(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

}
