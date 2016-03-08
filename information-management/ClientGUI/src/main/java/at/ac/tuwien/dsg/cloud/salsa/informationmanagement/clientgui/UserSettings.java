/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.QueryManager;

/**
 *
 * @author hungld
 */
public class UserSettings {

    public static String getBroker() {
        return "amqp://128.130.172.215";
    }

    public static String getBrokerType() {
        return "amqp";
    }

    public static String getUserName() {
        return "myClient";
    }
    
    public static QueryManager getQueryManager(){
        return new QueryManager(getUserName(), getBroker(), getBrokerType());
    }
}
