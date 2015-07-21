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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import java.util.HashMap;
import java.util.Map;

/**
 * This class map action with an instance ID. It says that: those actions are executed on these instances
 *
 * @author Duc-Hung Le
 */
public class ActionIDManager {

    //static Map<String, InstanceFullID> actions = new HashMap<>();
    static Map<String, SalsaMsgConfigureArtifact> actions = new HashMap<>();

//    public static class InstanceFullID {
//
//        String userName;
//        String service;
//        String topology;
//        String unit;
//        int instance;
//
//        public InstanceFullID() {
//        }
//
//        public InstanceFullID(String userName, String service, String topology, String unit, int instance) {
//            this.userName = userName;
//            this.service = service;
//            this.topology = topology;
//            this.unit = unit;
//            this.instance = instance;
//        }
//
//        public String getUserName() {
//            return userName;
//        }
//
//        public String getService() {
//            return service;
//        }
//
//        public String getTopology() {
//            return topology;
//        }
//
//        public String getUnit() {
//            return unit;
//        }
//
//        public int getInstance() {
//            return instance;
//        }
//
//    }

    public static void addAction(String actionID, SalsaMsgConfigureArtifact fullInstanceID) {
        actions.put(actionID, fullInstanceID);
    }

    public static void removeAction(String actionID) {
        actions.remove(actionID);
    }

    public static SalsaMsgConfigureArtifact getInstanceFullID(String actionID) {
        return actions.get(actionID);
    }
    
    public static String describe() {
        String actionIDs = "";
        for (Map.Entry<String, SalsaMsgConfigureArtifact> entry : actions.entrySet()) {
            SalsaMsgConfigureArtifact ai = entry.getValue();
            actionIDs += "Action: " + entry.getKey() + ":" + ai.getUser() + "/" + ai.getService() + "/" + ai.getUnit() + "/" + ai.getInstance() + ". \n";
        }
        return actionIDs;
    }

}
