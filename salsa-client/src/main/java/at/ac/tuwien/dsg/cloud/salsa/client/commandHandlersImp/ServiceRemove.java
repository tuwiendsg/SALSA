/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceRemove implements CommandHandler {

    @Argument(index = 0, usage = "ID for the deleted service")
    String serviceID;

    @Override
    public void execute() {
        String path = "/services/" + serviceID;
        RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.DELETE, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Undeploy all components and remove the cloud services";
    }

}
