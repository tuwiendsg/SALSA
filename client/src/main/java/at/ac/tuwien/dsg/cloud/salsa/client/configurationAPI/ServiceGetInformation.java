/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.configurationAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author hungld
 */
public class ServiceGetInformation implements CommandHandler {
    
    @Argument(index = 0, usage = "The name of the service to remove.", metaVar = "serviceName", required = true)
    String serviceName;

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/services/" + serviceName), RestHandler.HttpVerb.GET, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Get information of a service in JSON";
    }
    
}
