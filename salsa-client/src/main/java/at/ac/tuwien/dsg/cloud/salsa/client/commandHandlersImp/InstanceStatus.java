/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;

/**
 *
 * @author Duc-Hung LE
 */
public class InstanceStatus implements CommandHandler {

    @Override
    public void execute() {
        System.out.println("This function is not implemented yet");
    }

    @Override
    public String getCommandDescription() {
        return "Get running status of an instance. This function is not implemented yet";
    }
    
}
