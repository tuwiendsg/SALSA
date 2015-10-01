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
public class ServiceList implements CommandHandler {

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/viewgenerator/cloudservice/json/list"), RestHandler.HttpVerb.GET, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "List the current managed cloud services";
    }
    
}
