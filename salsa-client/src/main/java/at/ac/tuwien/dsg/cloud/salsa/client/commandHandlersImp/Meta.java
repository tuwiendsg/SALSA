/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;

/**
 *
 * @author Duc-Hung LE
 */
public class Meta implements CommandHandler {

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/manager/meta"), RestHandler.HttpVerb.GET, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Get metadata of the SALSA which is connected.";
    }
    
}
