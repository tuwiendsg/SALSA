/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;

/**
 *
 * @author Duc-Hung LE
 */
public class Syn implements CommandHandler {

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/manager/syn"), RestHandler.HttpVerb.GET, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Send message the synchronize pioneers.";
    }
    
}
