/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class InstanceRemove implements CommandHandler{

    // instance ID: serviceID/nodeID/instanceID
    @Argument(index = 0, usage = "The full id of the instance to be removed, should be: serviceID/unitID/isntanceID", metaVar = "instanceID", required = true)            
    String instanceID;
    
    @Override
    public void execute() {
        String[] ss = instanceID.split("/");
        if (ss.length==3){
            System.out.println("Removing instance: " + ss[0] +"/" + ss[1] +"/" + ss[2]);
            String path = "/services/"+ss[0]+"/nodes/"+ss[1]+"/instances/"+ss[2];
            RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.DELETE, null, null, null);
        } else {
            System.out.println("Error: The instance ID must be: serviceID/nodeID/instanceID");
        }
    }

    @Override
    public String getCommandDescription() {
        return "Remove an running instance.";
    }

}
