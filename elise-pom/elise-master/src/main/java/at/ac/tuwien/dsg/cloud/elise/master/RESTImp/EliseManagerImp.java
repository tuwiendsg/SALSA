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
package at.ac.tuwien.dsg.cloud.elise.master.RESTImp;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.CollectorDescription;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.Communication.QueryManager;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.OfferedServiceRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.IdentificationManager;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.CollectorArtifactManager;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Duc-Hung Le
 */
public class EliseManagerImp implements EliseManager {

    Logger logger = EliseConfiguration.logger;
    MessageClientFactory factory = MessageClientFactory.getFactory(EliseConfiguration.getBroker(), EliseConfiguration.getBrokerType());

    // FOR MANAGING COLLECTORS LOCALLY
    List<ConductorDescription> conductors = new ArrayList<>();

    @Override
    public String registerConductor(ConductorDescription conductor) {
        if (this.conductors == null) {
            this.conductors = new ArrayList<>();
        }
        logger.debug("Registering a conductor. Info: {}", conductor.toJson());
        this.conductors.add(conductor);
        logger.debug("Registered a collector: " + conductor.getId());
        return "ELISE registered a new conductor: " + conductor.getId();
    }

    @Override
    public String updateConductor(ConductorDescription collector) {
        if (removeConductor(collector.getId()) != null) {
            return "ELISE updated an existed collector" + registerConductor(collector);
        }
        return null;
    }

    @Override
    public ConductorDescription getConductor(String collectorID) {
        logger.debug("Getting conductor list: {} found", this.conductors.size());
        for (ConductorDescription desp : this.conductors) {
            if (desp.getId().equals(collectorID)) {
                return desp;
            }
        }
        return null;
    }

    @Override
    public String removeConductor(String collectorID) {
        for (ConductorDescription desp : this.conductors) {
            if (desp.getId().equals(collectorID)) {                
                this.conductors.remove(desp);                
                return "ELISE removed a collector" + collectorID;
            }
        }
        return null;
    }

    @Override
    public List<ConductorDescription> getConductorList() {
        return this.conductors;
    }

    @Override
    public void runConductorViaSalsa(String pioneerID) {
        if (pioneerID.equals("salsa")) {
            // call SALSA rest to run conductor
        } else {
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_addConductor, EliseConfiguration.getEliseID(), SalsaMessageTopic.getPioneerTopicByID(pioneerID), "", "");
            factory.getMessagePublisher().pushMessage(msg);
        }
    }
    
    /**
     * Elise master publish a message to inform a conductor to download and inject collectors The name of the collector will be used as the parameter to
     * download the artifact
     *
     * @param configuration the collector configuration, e.g. endpoint=http://example.com; user=test; password=test
     * @param conductorID the ID of conductor to push
     * @param collectorName the name of the collector
     */
    @Override
    public void pushCollectorToConductor(String configuration, String conductorID, String collectorName) {
        MessagePublishInterface publish = factory.getMessagePublisher();
        String artURL = EliseConfiguration.getRESTEndpoint() + "/manager/collector/" + collectorName;
        CollectorDescription collector = new CollectorDescription(collectorName, conductorID, artURL, configuration);
        // message: send to NOTIFICATION
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.elise_addCollector, EliseConfiguration.getEliseID(), EliseQueueTopic.QUERY_TOPIC, null, collector.toJson());
        publish.pushMessage(msg);
    }

    @Override
    public Response getCollectorArtifact(String collectorName) {
        logger.debug("Getting collector artifact and return: {}", collectorName);
        String localArtifactFile = CollectorArtifactManager.getCollectors().get(collectorName);
        if (localArtifactFile == null) {
            logger.debug("Local artifact file is not found: {}", localArtifactFile);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.debug("Found local artifact file: {}", localArtifactFile);

        File file = new File(localArtifactFile);
        String fileName = file.getName();
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }
    
      
    @Override
    public String getCollectorNameList(){
        Map<String, String> map = CollectorArtifactManager.getCollectors();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (IOException ex) {
            return "Cannot get the collection name list!";
        }        
    }

    /**
     * Publish a message to ask conductors to register themselves.
     */
    @Override
    public void ResynConductors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // FOR GENERAL TASKS
    @Autowired
    OfferedServiceRepository surepo;

    @Override
    public String health() {
        System.out.println("Health checked");
        return EliseConfiguration.getEliseID();
    }

    @Override
    public String cleanDB() {
        surepo.cleanDataBase();
        return "DB Cleaned";
    }

    // FOR IDENTIFICATION
    @Override
    public List<GlobalIdentification> updateComposedIdentification(LocalIdentification si, String possibleGlobalID) {
        IdentificationManager im = new IdentificationManager();
        return im.searchAndUpdate(si, possibleGlobalID);
    }
    
    @Override
    public void deleteGlobalIdentification(@PathParam("globalID") String globalID){
        IdentificationManager im = new IdentificationManager();
        im.deleteAndUpdate(globalID);
    }

    @Override
    public String getQueryInformation(String queryUUID) {
        logger.debug("Writing query management info to Json");
        Map<String, EliseQueryProcessNotification.QueryProcessStatus> map = QueryManager.getQueryStatusAll(queryUUID);
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.debug("writing ...");
            String s = mapper.writeValueAsString(map);
            logger.debug("done...");
            return s;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // for query management
}
