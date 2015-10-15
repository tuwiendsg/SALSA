/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryProcessNotification;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author Duc-Hung LE
 */
public class InstanceInfoCollect implements CommandHandler {

    @Override
    public void execute() {
        // I think it is not important here yet, the conductor do not care about this query
        EliseQuery query = new EliseQuery(ServiceCategory.ExecutableApp);
        String queryUUID = RestHandler.callRest(Main.getEliseAPI("/communication/queryUnitInstance"), RestHandler.HttpVerb.POST, query.toJson(), MediaType.APPLICATION_JSON, null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(InstanceInfoCollect.class.getName()).log(Level.SEVERE, null, ex);
        }
        // now query if there are update process
        if (queryUUID != null) {            
            Map<String, EliseQueryProcessNotification.QueryProcessStatus> map;
            ObjectMapper mapper = new ObjectMapper();
            try {
                int total = 0;
                int working = 0;
                int done = 0;
                int coolDown = 10;
                while (true) {
                    String progress = RestHandler.callRest(Main.getEliseAPI("/manager/query"), RestHandler.HttpVerb.GET, query.toJson(), null, null);
                    if (progress == null && coolDown <= 0) {
                        System.out.println("Waiting for the conductor to receive collection command [" + coolDown + "]...");
                        Thread.sleep(2000);
                        coolDown = coolDown - 1;
                        continue;
                    }
                    map = mapper.readValue(progress, new TypeReference<HashMap<String, EliseQueryProcessNotification.QueryProcessStatus>>() {
                    });
                    for (Map.Entry<String, EliseQueryProcessNotification.QueryProcessStatus> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + ":" + entry.getValue());
                    }

                    if (working == 0 && done == total && coolDown <= 0) {
                        System.out.println("Done !");
                        break;
                    }
                    if (coolDown >= 0) {
                        coolDown = coolDown - 1;
                    }
                    System.out.println("Waiting for the conductor to complete the collection [" + coolDown + "]...");
                    Thread.sleep(2000);
                }
            } catch (IOException ex) {
                System.out.println("Error: Cannot call the API !");
            } catch (InterruptedException ex) {
                System.out.println("Interrupt !");
            }
        }

    }

    @Override
    public String getCommandDescription() {
        return "Ask collector to start gathering the information. The process may take time.";
    }

}
