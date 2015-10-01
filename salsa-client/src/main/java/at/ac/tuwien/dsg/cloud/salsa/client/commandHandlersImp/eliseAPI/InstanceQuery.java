/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class InstanceQuery implements CommandHandler{

    // instance ID: serviceID/nodeID/instanceID
    @Argument(index = 0, usage = "The full id of the instance to be removed, should be: serviceID/unitID/isntanceID", metaVar = "instanceID", required = true)
    String instanceID;
    
    @Override
    public void execute() {
         String path = "/unitinstance/" + instanceID;
        RestHandler.callRest(Main.getEliseAPI(path), RestHandler.HttpVerb.GET, null, null, MediaType.APPLICATION_JSON);
    }

    @Override
    public String getCommandDescription() {
        return "Get the information of an instance by ID.";
    }
    
}
