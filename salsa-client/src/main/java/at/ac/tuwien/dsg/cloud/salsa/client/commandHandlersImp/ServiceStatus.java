/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceStatus implements CommandHandler{

    @Argument(index = 0, required = true, metaVar = "serviceID", usage = "ID for the service to check")
    String serviceID;
    
    @Override
    public void execute() {
        //String path = "/services/" + serviceID;  
        String path = "/viewgenerator/cloudservice/json/compact/" + serviceID;
        RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.GET, null, MediaType.APPLICATION_JSON, null);
    }

    @Override
    public String getCommandDescription() {
        return "Get the configuration status of the cloud service.";
    }
    
}
