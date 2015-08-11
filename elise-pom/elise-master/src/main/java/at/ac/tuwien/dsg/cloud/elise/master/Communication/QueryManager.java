/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.Communication;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification.QueryProcessStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung Le
 */
public class QueryManager {
    static Logger logger = EliseConfiguration.logger;
    // Map<queryID, Map<ELISEID,QueryProcessStatus>>
    public static Map<String, Map<String, QueryProcessStatus>> queries = new HashMap();

    public static void updateQueryStatus(String noti) {
        logger.debug("Update query status: " + noti);
        updateQueryStatus(EliseQueryProcessNotification.fromJson(noti));
    }    
    
    public static void updateQueryStatus(EliseQueryProcessNotification noti) {
        logger.debug("Updating the query status ....");
        if (!noti.getFromELISE().equals(EliseConfiguration.getEliseID())) {
            logger.debug("noti is not belong to the ELISE: " + EliseConfiguration.getEliseID()+", noti:" + noti.toJson());
            return;
        }
        logger.debug("OK, the status is updating for the query that this ELISE shot out....");
        Map<String, QueryProcessStatus> map = queries.get(noti.getQueryID());
        if (map == null){   // first update to the status
            logger.debug("Create new Map<EliseID, QueryProcessStatus> as the first time");
            map = new HashMap<>();
            queries.put(noti.getQueryID(), map);
            logger.debug("And put into the QueryManager, done. Currently it has: " + queries.size() + "queries");            
        }
        
        if (map.get(noti.getProcessingELISE())!=null && map.get(noti.getProcessingELISE()).equals(QueryProcessStatus.DONE)) {
            logger.debug("The query is done already, no update! ");
            return; // if done, no update
        }
        logger.debug("Updating query status....");       
        map.put(noti.getProcessingELISE(), noti.getStatus());
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.debug("Updated: " + mapper.writeValueAsString(map));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Map<String, QueryProcessStatus> getQueryStatusAll(String queryID) {
        logger.debug("Querying status of query: " + queryID);
        
        return queries.get(queryID);
    }

    public static QueryProcessStatus getQueryStatusOfElise(String queryID, String fromELISE) {
        if (queries.get(queryID) != null) {
            return queries.get(queryID).get(fromELISE);
        } 
        return null;
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

    public static Map<String, Map<String, QueryProcessStatus>> getQueries() {
        return queries;
    }
    
}
