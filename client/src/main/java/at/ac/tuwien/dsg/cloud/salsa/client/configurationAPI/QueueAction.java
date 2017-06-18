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
public class QueueAction implements CommandHandler {

    @Argument(index = 0, usage = "The name of the service.", metaVar = "serviceName", required = true)
    String serviceName;

    @Argument(index = 1, usage = "The name of the unit.", metaVar = "unitName", required = true)
    String unitName;

    @Argument(index = 2, usage = "The name of the instance.", metaVar = "instanceId", required = true)
    String instanceId;

    @Argument(index = 3, usage = "The name action.", metaVar = "actionName", required = true)
    String actionName;

    @Override
    public void execute() {
        RestHandler.callRest(Main.getSalsaAPI("/services/" + serviceName + "/nodes/" + unitName + "/instances/" + instanceId + "/action_queue/" + actionName), RestHandler.HttpVerb.POST, null, null, null);
    }

    @Override
    public String getCommandDescription() {
        return "Uneploy an instance of an unit";
    }

}
