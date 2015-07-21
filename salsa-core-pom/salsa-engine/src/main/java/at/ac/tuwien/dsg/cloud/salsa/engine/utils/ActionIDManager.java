/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import java.util.HashMap;
import java.util.Map;

/**
 * This class map action with an instance ID. It says that: those actions are executed on these instances
 *
 * @author hungld
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
