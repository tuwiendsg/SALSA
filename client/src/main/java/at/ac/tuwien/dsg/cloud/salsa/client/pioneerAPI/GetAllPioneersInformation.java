/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.pioneerAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.configurationAPI.*;
import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author hungld
 */
public class GetAllPioneersInformation implements CommandHandler {

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/manager/pioneers/json"), RestHandler.HttpVerb.GET, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Get information of all pioneers in detail.";
    }

}
