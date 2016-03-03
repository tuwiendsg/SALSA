/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.handler;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.CommandHandler;

/**
 *
 * @author hungld
 */
public class Syn implements CommandHandler {

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCommandDescription() {
        return "Send request, wait for a few second, then give summary about available D-Elise local manager.";
    }
    
}
