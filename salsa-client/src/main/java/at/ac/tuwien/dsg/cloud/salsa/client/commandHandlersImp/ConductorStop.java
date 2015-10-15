/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ConductorStop implements CommandHandler {
    
    @Argument(index =0, required = false, usage = "The ID of the pioneer to run the conductor. Please use 'syn' and 'meta' command to see available pioneer.")
    String conductorID;

    @Override
    public void execute() {
        if (conductorID == null || conductorID.equals("")){
            RestHandler.callRest(Main.getSalsaAPI("/manager/conductor/"+conductorID.trim()), RestHandler.HttpVerb.DELETE, null, null, MediaType.TEXT_PLAIN);
        } else {
            System.out.println("Stopped conductor via pioneer is not implemented yet, just use empty parameter!");
        }
    }

    @Override
    public String getCommandDescription() {
        return "Stop a conductor by ID or at salsa-engine";
    }   
    
}
