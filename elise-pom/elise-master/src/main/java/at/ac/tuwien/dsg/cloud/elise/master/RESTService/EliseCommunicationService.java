/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTService;

import at.ac.tuwien.dsg.cloud.elise.master.Communication.QueryManager;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.wrapper.UnitInstanceWrapper;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.CollectorsForConductor;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung Le
 */
@Path("/communication")
public class EliseCommunicationService {

    static Logger logger = EliseConfiguration.logger;
    static String listenerTopic = "at.ac.tuwien.dsg.comot.elise.listener";
    MessageClientFactory factory = MessageClientFactory.getFactory(EliseConfiguration.getBroker(), EliseConfiguration.getBrokerType());

    static int eliseCounter = 0;

    /**
     * Get the information of all conductors
     *
     * @return the number of conductors
     */
    @GET
    @Path("/count")
    public String count() {
        String uuid = UUID.randomUUID().toString();
        final List<ConductorDescription> conductors = new ArrayList<>();
        eliseCounter = 0;

        MessageSubscribeInterface sub = factory.getMessageSubscriber(new SalsaMessageHandling() {

            @Override
            public void handleMessage(SalsaMessage salsaMessage) {

                ConductorDescription conductor = ConductorDescription.fromJson(salsaMessage.getPayload());
                if (conductor == null) {
                    logger.error("Cannot unmarshall the ConductorDescription: {}", salsaMessage.getPayload());
                    return;
                }
                eliseCounter += 1;
                logger.debug("Get a response from ELISE: " + eliseCounter + "," + conductor.toJson());
                conductors.add(conductor);
            }
        });
        sub.subscribe(EliseQueueTopic.getFeedBackTopic(uuid));
        MessagePublishInterface pub = factory.getMessagePublisher();

        pub.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, EliseConfiguration.getEliseID(), EliseQueueTopic.QUERY_TOPIC, EliseQueueTopic.getFeedBackTopic(uuid), ""));

        try {
            Thread.sleep(5000L);        // wait 5 secs for other elises to answer
        } catch (InterruptedException ex) {
            logger.debug(ex.getMessage());
        }

        logger.debug("Found " + eliseCounter + " Conductor(s)");
        String result = eliseCounter + "";
        for (ConductorDescription s : conductors) {
            result += "," + s.getId();
        }
        //sub.disconnect();
        return result;
    }

    static long startTime;
    int count;
    String rtLogFile;

    /**
     * Send a query to collect the information
     *
     * @param query a query to specify which information to be collected
     * @param isUpdated the information will be update continously
     * @param isNotified the change will be notify to the topic: at.ac.tuwien.dsg.elise.notification
     * @return a String of the Wrapper that wrap multiple service instances
     */
    @POST
    @Path("/queryUnitInstance")
    @Consumes(MediaType.APPLICATION_JSON)
    public String querySetOfInstance(EliseQuery query,
            @DefaultValue("false") @QueryParam("isUpdated") final boolean isUpdated,
            @DefaultValue("false") @QueryParam("notify") final boolean isNotified) {
        logger.debug("Broadcast a query to gather instances... Query: " + query.toString());
        final String uuid = UUID.randomUUID().toString();
        logger.debug("UUID of the request: " + uuid);

        // clean local DB
        //EliseManager eliseDB = (EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider()));
        //eliseDB.cleanDB();
        count = 0;
        startTime = Calendar.getInstance().getTimeInMillis();
        rtLogFile = ("log/rt_log_" + startTime + "." + uuid);

        MessageSubscribeInterface sub = factory.getMessageSubscriber(new SalsaMessageHandling() {
            Map<String, String> answeredElises = new HashMap();

            @Override
            public void handleMessage(SalsaMessage message) {
                toHandleMessageAndSaveData(message, answeredElises, isNotified, uuid);
            }
        });

        logger.debug("Subscribing the topic: " + EliseQueueTopic.getFeedBackTopic(uuid));
        sub.subscribe(EliseQueueTopic.getFeedBackTopic(uuid));
        logger.debug("Subscribe to feedback topic done, now push request ...");
        MessagePublishInterface pub = factory.getMessagePublisher();

        pub.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_queryManyInstances, EliseConfiguration.getEliseID(), EliseQueueTopic.QUERY_TOPIC, EliseQueueTopic.getFeedBackTopic(uuid), query.toJson()));
        logger.debug("Push message done, just waiting for the message ...");
        //unsubscribe(sub, 120);

        return uuid;
    }

    // the collector module can execute with single domainID, but from this level, it is imposible?
    // must use the querySetOfInstance.
    @Deprecated
    @POST
    @Path("/queryUnitInstance/{domainID}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String querySingleInstance(@PathParam("domainID") String instanceID,
            @DefaultValue("false") @QueryParam("isUpdated") final boolean isUpdated,
            @DefaultValue("false") @QueryParam("notify") final boolean isNotified) {
        String uuid = UUID.randomUUID().toString();
        MessageSubscribeInterface sub = factory.getMessageSubscriber(new SalsaMessageHandling() {
            Map<String, String> answeredElises = new HashMap();

            @Override
            public void handleMessage(SalsaMessage message) {
                toHandleMessageAndSaveData(message, answeredElises, isNotified, uuid);
            }
        });

        logger.debug("Subscribing the topic: " + EliseQueueTopic.getFeedBackTopic(uuid));
        sub.subscribe(EliseQueueTopic.getFeedBackTopic(uuid));
        
        logger.debug("Subscribe to feedback topic done, now push request ...");
        MessagePublishInterface pub = factory.getMessagePublisher();

        pub.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_querySingleInstance, EliseConfiguration.getEliseID(), EliseQueueTopic.QUERY_TOPIC, EliseQueueTopic.getFeedBackTopic(uuid), instanceID));
        logger.debug("Push message done, just waiting for the message ...");

        return uuid;
    }

    private void toHandleMessageAndSaveData(SalsaMessage message, Map<String, String> answeredElises, boolean isNotified, String uuid) {
        String fromElise = message.getFromSalsa();
        String fromTopic = message.getTopic();
        long originTime = message.getTimeStamp();

        String jsonHeader = "Count, FromElise, responseTime, updateTime \n";
        //increateCountAndWriteData(jsonHeader);

        logger.debug("Retrieve the answer from ELISE: " + fromElise + ", topic: " + fromTopic + ", orginial timestamp: " + originTime);
        if (message.getFromSalsa().equals(EliseConfiguration.getEliseID())) {
            logger.debug("Message from the same one, no need to add: " + fromElise + ", topic: " + fromTopic);
            answeredElises.put(fromElise, fromTopic);
            // however, still need to update query status to Done (local update)                              
            QueryManager.updateQueryStatus(new EliseQueryProcessNotification(uuid, EliseConfiguration.getEliseID(), fromElise, EliseQueryProcessNotification.QueryProcessStatus.DONE));
            return;
        }

        if (fromTopic != null) {
            if (fromTopic.equals(answeredElises.get(fromElise))) {
                logger.debug("Duplicate subscribing message from ELISE: " + fromElise + ", topic: " + fromTopic);
                return;
            }
            answeredElises.put(fromElise, fromTopic);
        }
        ObjectMapper mapper = new ObjectMapper();
        JavaType javatype = mapper.getTypeFactory().constructCollectionType(Set.class, UnitInstance.class);
        try {
            UnitInstanceWrapper wrapper = (UnitInstanceWrapper) mapper.readValue(message.getPayload(), UnitInstanceWrapper.class);
            Set<UnitInstance> uis = wrapper.getUnitInstances();
            logger.debug("Recieved " + uis.size() + " unitinstance. Saving....");
            UnitInstanceDAO unitInstanceDAO = (UnitInstanceDAO) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceDAO.class, Collections.singletonList(new JacksonJsonProvider()));
            for (UnitInstance u : uis) {
                unitInstanceDAO.addUnitInstance(u);
            }
            long now = Calendar.getInstance().getTimeInMillis();
            long responseTime = now - startTime;
            long updateTime = now - message.getTimeStamp();

            String jsonLine = padLeft(count + "", 3) + ","
                    + padLeft(message.getFromSalsa(), 20) + ","
                    + padLeft(responseTime + "", 7) + ","
                    + updateTime + "\n";

            //String jsonLine = count + "," + message.getFromElise() + "," + responseTime + "," + updateTime + "\n";
            logger.debug("Adding done in: " + responseTime + " ms, for " + uis.size() + " instances");
            increateCountAndWriteData(jsonLine);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EliseCommunicationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (isNotified) {   // notify that a ELISE has answered. record the answer time
            MessagePublishInterface pub = factory.getMessagePublisher();
            // this is the response, so its "fromTopic" is the feedback topic of the query
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, EliseConfiguration.getEliseID(), EliseQueueTopic.NOTIFICATION_TOPIC, "", buildNotification(fromTopic, originTime));
            //pub.pushCustomData(buildNotification(fromTopic, originTime), EliseQueueTopic.NOTIFICATION_TOPIC);

//                    long currentTimeStamp = System.currentTimeMillis();
//                    long derivation = currentTimeStamp - originTime;                    
        }

        // update query status to Done (local update)                              
        QueryManager.updateQueryStatus(new EliseQueryProcessNotification(uuid, EliseConfiguration.getEliseID(), fromElise, EliseQueryProcessNotification.QueryProcessStatus.DONE));

    }

    private void unsubscribe(final MessageSubscribeInterface sub, long delayInSecond) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sub.disconnect();
                    }
                },
                delayInSecond * 1000
        );
    }

    private String buildNotification(String feedbackTopic, long timeStamp) {
        Date date = new Date(timeStamp);
        return feedbackTopic + "," + timeStamp + "," + date.toString();
    }

    public String health() {
        return "Listening";
    }

    private synchronized void increateCountAndWriteData(String line) {
        try {
            File file = new File(this.rtLogFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(line);
            bufferWritter.close();
            this.count += 1;
        } catch (IOException e) {
            this.logger.error("Cannot create log file for response time !");
        }
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }
}
