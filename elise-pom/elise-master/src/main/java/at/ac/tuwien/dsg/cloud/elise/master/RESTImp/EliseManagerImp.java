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

import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.Communication.QueryManager;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.OfferedServiceRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.IdentificationManager;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Duc-Hung Le
 */
public class EliseManagerImp implements EliseManager {

    Logger logger = EliseConfiguration.logger;

    // FOR MANAGING COLLECTORS
    List<ConductorDescription> conductors = new ArrayList<>();

    @Override
    public String registerConductor(ConductorDescription collector) {        
        if (this.conductors == null) {
            this.conductors = new ArrayList<>();
        }
        logger.debug("Registering a collector. Info: {}", collector.toJson());
        this.conductors.add(collector);
        logger.debug("Registered a collector: " + collector.getId());
        return "ELISE registered a new collector: " + collector.getId();
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
    public List<ConductorDescription> getCollectorList() {
        return this.conductors;
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
    public GlobalIdentification updateComposedIdentification(LocalIdentification si) {
        IdentificationManager im = new IdentificationManager();
        return im.searchAndUpdate(si);
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
