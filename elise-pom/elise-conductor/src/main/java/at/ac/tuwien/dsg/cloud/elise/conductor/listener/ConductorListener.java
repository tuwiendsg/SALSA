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
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.CollectorDescription;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;

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
        logger.debug("Loading all the jar...");
        instanceCollectorClasses = loadAllJar();
        if (instanceCollectorClasses == null || instanceCollectorClasses.isEmpty()) {
            logger.debug("No collector class is load at the begining !");
        } else {
            for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
                logger.debug("Collector class found: {}", c.getName());
            }
        }

        // Listening to the query
        logger.debug("Subscribing to the control topic : {}", EliseQueueTopic.QUERY_TOPIC);

        MessageSubscribeInterface subscriber = factory.getMessageSubscriber(new SalsaMessageHandling() {
            Map<String, String> answeredElises = new HashMap();

            @Override
            public void handleMessage(SalsaMessage message) {
                String fromElise = message.getFromSalsa();
                String feedbackTopic = message.getFeedbackTopic();
                logger.debug("Retrieve the request from ELISE: " + fromElise + ", feedback topic: " + feedbackTopic);
                if (feedbackTopic != null) {
                    if (feedbackTopic.equals(this.answeredElises.get(fromElise))) {
                        logger.debug("Neglect duplicated subscribing message from ELISE: " + fromElise + ", topic: " + feedbackTopic);
                        return;
                    }
                    this.answeredElises.put(fromElise, feedbackTopic);
                }

                logger.debug("Listenner got a control message from ELISE of type: " + message.getMsgType());

                String response;
                SalsaMessage resMsg = null;
                switch (message.getMsgType()) {
                    case discover:
                        register();
                    case elise_queryManyInstances:
                    case elise_querySingleInstance: {
                        // first send a notification to say that this ELISE is processing the info
                        MessagePublishInterface publish = factory.getMessagePublisher();
                        String queryUUID = message.getFeedbackTopic().substring(message.getFeedbackTopic().lastIndexOf(".") + 1);
                        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_queryProcessNotification, ConductorConfiguration.getConductorID(), EliseQueueTopic.NOTIFICATION_TOPIC, null,
                                new EliseQueryProcessNotification(queryUUID, message.getFromSalsa(), ConductorConfiguration.getConductorID(), EliseQueryProcessNotification.QueryProcessStatus.PROCESSING).toJson()));
                        Set<UnitInstance> instances = null;
                        if (message.getMsgType().equals(SalsaMessage.MESSAGE_TYPE.elise_queryManyInstances)) {
                            logger.debug("Start to run all collector to collect instances with query string: {}", message.getPayload());
                            instances = runAllUnitInstanceCollector(message.getPayload(), false);
                        } else if (message.getMsgType().equals(SalsaMessage.MESSAGE_TYPE.elise_querySingleInstance)) {
                            logger.debug("Start to run all collector to collect instance with id: {}", message.getPayload());
                            instances = runAllUnitInstanceCollector(message.getPayload(), true);
                        }
                        if (instances == null) {
                            logger.error("Error happens when run all collector !");
                            break;
                        }
                        UnitInstanceWrapper wrapper = new UnitInstanceWrapper(instances);
                        response = wrapper.toJson();
                        logger.debug("This conductor got {} instances, wrapped !", instances.size());

                        resMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_instanceInfoUpdate, ConductorConfiguration.getConductorID(), feedbackTopic, null, response);
                        break;
                    }
                    case elise_queryProvider:
                        response = "{\"message\":\"Not support query provider yet\"}";
                        break;
                    case elise_instanceInfoUpdate:
                        return;
                    case elise_addCollector:
                        resMsg = null;
                        CollectorDescription collector = CollectorDescription.fromJson(message.getPayload());
                        if (collector.getAssignedConductorID().equals(ConductorConfiguration.getConductorID())) {
                            try {
                                URL url = new URL(collector.getArtifactURL());
                                File folder = new File(ConductorConfiguration.getCollectorFolder(collector.getName()));
                                folder.mkdirs();
                                File file = new File(ConductorConfiguration.getCollectorFolder(collector.getName()) + "/" + collector.getName() + "-collector.jar");
                                logger.debug("Ok, now downloading artifact of collector {}, from: {}, save to: {}", collector.getName(), url.toString(), file.getAbsolutePath());
                                FileUtils.copyURLToFile(url, file);

                                // create configuration file
                                if (collector.getConfigurations() != null && !collector.getConfigurations().isEmpty()) {
                                    StringBuilder sb = new StringBuilder();
                                    for (String s : collector.getConfigurations()) {
                                        logger.debug("Found a configuration: {}", s);
                                        sb.append(s).append("\n");
                                    }
                                    File adaptorConf = new File(ConductorConfiguration.getCollectorFolder(collector.getName()) + "/adaptor.conf");
                                    FileUtils.writeStringToFile(adaptorConf, sb.toString());
                                }
                            } catch (MalformedURLException ex) {
                                logger.error("The URL to download collector artifact is incorrect");
                                ex.printStackTrace();
                                break;
                            } catch (IOException ex) {
                                logger.error("Cannot download artifact, or cannot copy to {}", ConductorConfiguration.getCollectorFolder(collector.getName()));
                                ex.printStackTrace();
                                break;
                            }
                        }
                        // try to load all Jar again
                        logger.debug("Trying to reload all collector ...");
                        instanceCollectorClasses = loadAllJar();
                        if (instanceCollectorClasses == null || instanceCollectorClasses.isEmpty()) {
                            logger.debug("No collector class is load at the begining !");
                        } else {
                            logger.debug("All collector load done !");
                            for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
                                logger.debug("  --> Collector class found: {}", c.getName());
                            }
                        }

                        break;
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

        logger.debug("Registering with salsa-engine...");
        register();

        logger.info("Conductor initiation is done. ID: {}", ConductorConfiguration.getConductorID());
    }

    private static void register() {
        logger.debug("  --- Basic conductDesp start");
        String response;
        SalsaMessage resMsg = null;
        ConductorDescription conductDesp = new ConductorDescription(ConductorConfiguration.getConductorID(), ConductorConfiguration.getELISE_IP());
        logger.debug("  --- Basic conductDesp done");
        if (instanceCollectorClasses != null) {
            for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
                try {
                    logger.debug("  --- Trying to create instance");
                    UnitInstanceCollector u = (UnitInstanceCollector) urlClassLoader.loadClass(c.getName()).newInstance();
                    logger.debug("  --- Trying to create instance DONE");
                    for (String s : u.readAllAdaptorConfig()) {
                        logger.debug("   ALL-CONFIG-ARRAY: {}", s);
                    }
                    logger.debug("  --- Trying to create instance and get all config DONE");
                    conductDesp.hasCollector(new CollectorDescription(u.getName(), ConductorConfiguration.getConductorID(), "N/A", u.readAllAdaptorConfigOneString()));
                    logger.debug("When creating conductor description, we have collector: {}", u.getName());
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                    logger.error("Cannot create instance of class: {}", c.toString(), ex);
                }
            }
        }
        response = conductDesp.toJson();
        logger.debug("Sending back the conductor description: {}", response);
        resMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_conductorActivated, ConductorConfiguration.getConductorID(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", response);
        logger.debug("Sending message: " + resMsg.toJson());
        MessagePublishInterface publisher = factory.getMessagePublisher();
        logger.debug("Pushing registration data for conductor...");
        publisher.pushMessage(resMsg);
    }

//    private static void registerConductor() {
//        logger.info("Registering the collector: " + ConductorConfiguration.getConductorID());
//
//        MessagePublishInterface publish = factory.getMessagePublisher();
//        // TODO: Later, publish more detail about the conductor, e.g. list of collector
//        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, ConductorConfiguration.getConductorID(), EliseQueueTopic.NOTIFICATION_TOPIC, "", "");
//        publish.pushMessage(msg);
//        logger.info("Registering message is published, not sure an ELISE can received it ! Note: we need to unicast, or ID checking.");
//    }
    // query all instance
    private static Set<UnitInstance> runAllUnitInstanceCollector(String queryOrDomainID, boolean isSingleInstanceQuery) {
        logger.debug("Execute all the collectors...");
        Set<UnitInstance> unitInstances = new HashSet<>();

        for (Class<? extends UnitInstanceCollector> c : instanceCollectorClasses) {
            try {
                logger.debug("Create collector object from class: {}", c.getName());
                UnitInstanceCollector collector = c.newInstance();
                
                if (collector != null) {
                    logger.debug("Now we have the collector: {}, classname:", collector.getName(), collector.getClass().getName());
                } else {
                    logger.error("No no, the class {} cannot be initiated", c.getName());
                    continue;
                }
                if (isSingleInstanceQuery) {
                    logger.debug("Calling collection for single ID");
                    unitInstances.add(collector.collectInstanceByID(queryOrDomainID));
                } else {
                    logger.debug("Calling collection for all instances");
                    try {
                        logger.debug("debug call 1");
                        Set<UnitInstance> tmpVar = collector.collectAllInstance();
                        logger.debug("debug call 2");
                        unitInstances.addAll(tmpVar);
                        logger.debug("debug call 3");
                    } catch (Exception e) {
                        logger.debug("Error !");
                        logger.error(e.getMessage(), e);
                        logger.debug("End Error Message!");
                        e.printStackTrace();
                    }

                }
                logger.debug("All the collection has done !");
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
        UnitInstance instance = aCollector.collectInstanceByID(domainID);
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
        if (folders == null) {
            logger.debug("There is no collector module loaded...");
            return null;
        }
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
