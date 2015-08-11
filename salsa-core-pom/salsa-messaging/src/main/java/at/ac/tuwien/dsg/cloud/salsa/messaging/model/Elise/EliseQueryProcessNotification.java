/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 */
public class EliseQueryProcessNotification {

    String queryID;
    String fromELISE;
    String processingELISE;
    QueryProcessStatus status;

    public enum QueryProcessStatus {
        DONE, PROCESSING
    }

    public EliseQueryProcessNotification() {
    }

    public EliseQueryProcessNotification(String queryID, String fromELISE, String processingELISE, QueryProcessStatus status) {
        this.queryID = queryID;
        this.fromELISE = fromELISE;
        this.processingELISE = processingELISE;
        this.status = status;
    }

    public String getQueryID() {
        return queryID;
    }

    public String getFromELISE() {
        return fromELISE;
    }

    public String getProcessingELISE() {
        return processingELISE;
    }

    public QueryProcessStatus getStatus() {
        return status;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static EliseQueryProcessNotification fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, EliseQueryProcessNotification.class);
        } catch (IOException ex) {
            System.out.println("Cannot convert the QueryProcessNotification from the json: " + json);
            ex.printStackTrace();
            return null;
        }
    }

}
