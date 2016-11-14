/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.messaging.protocol;

/**
 *
 * @author Duc-Hung Le
 */
public class EliseQueueTopic {

    private static final String prefix = "at.ac.tuwien.dsg.cloud.elise";
    // topic for broadcasting the ELISE query and management from Master to Conductor
    public static final String QUERY_TOPIC = prefix + ".query";
    // Topic for data transfer back, this is genereted
    public static final String FEEDBACK_TOPIC = prefix + ".feedback.";
    // EliseMaster listens to: conductor registering, query process notification
    public static final String NOTIFICATION_TOPIC = prefix + ".notification";
    

    public static String getFeedBackTopic(String feedBackID) {
        return prefix + ".feedback." + feedBackID;
    }
}
