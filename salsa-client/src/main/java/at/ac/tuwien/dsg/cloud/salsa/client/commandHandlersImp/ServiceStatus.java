/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceStatus implements CommandHandler{

    @Argument(index = 0, usage = "ID for the service to check")
    String serviceID;
    
    @Override
    public void execute() {
        String path = "/services/" + serviceID;        
        RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.GET, null, null, MediaType.TEXT_XML);
    }

    @Override
    public String getCommandDescription() {
        return "Get the configuration status of the cloud service";
    }
    
}
