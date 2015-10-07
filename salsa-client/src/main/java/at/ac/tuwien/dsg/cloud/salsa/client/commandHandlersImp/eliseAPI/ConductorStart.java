/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ConductorStart implements CommandHandler {
    
    @Argument(index =0, required = false, usage = "The ID of the pioneer to run the conductor. Please use 'syn' and 'meta' command to see available pioneer.")
    String pioneerID;

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCommandDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
