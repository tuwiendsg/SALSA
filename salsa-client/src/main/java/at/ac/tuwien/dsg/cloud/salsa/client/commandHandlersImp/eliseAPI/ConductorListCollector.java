/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;

/**
 *
 * @author Duc-Hung LE
 */
public class ConductorListCollector implements CommandHandler  {

    @Override
    public void execute() {
        RestHandler.callRest(Main.getEliseAPI("/manager/collector"), RestHandler.HttpVerb.GET, null, null, null);        
    }

    @Override
    public String getCommandDescription() {
        return "Get the list of available collector plugins.";
    }
    
}
