/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model;

import java.util.UUID;

/**
 *
 * @author hungld
 */
public class SalsaMessageTopic {

    private static final String PREFIX = "ac.at.tuwien.dsg.cloud.salsa.";
    public static final String CENTER_REQUEST_PIONEER = PREFIX + "center.request";    
    public static final String PIONEER_SYNC = PREFIX + "pioneer.sync";
    public static final String PIONEER_UPDATE_STATE = PREFIX + "pioneer.state";
    public static final String PIONEER_LOG = PREFIX + "pioneer.log";

//    public static String generateRequestID() {
//        return UUID.randomUUID().toString();
//    }
//
//    public static String getPioneerSyncTopic() {
//        return PIONEER_SYNC;
//    }

    
    
}
