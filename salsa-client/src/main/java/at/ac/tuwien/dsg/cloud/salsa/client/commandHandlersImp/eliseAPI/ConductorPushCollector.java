/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Duc-Hung LE
 */
public class ConductorPushCollector implements CommandHandler  {
    
    @Argument(index = 0, required = true, metaVar = "conductorID", usage = "The ID of the conductor to push the collector")
    String conductorID;
    
    @Argument(index = 1, required = true, metaVar = "collectorName", usage = "The name of the collector")
    String collectorName;
    
    @Option(name = "-s",metaVar = "settings", usage = "The settings of the collector, e.g. -s 'endpoint=http://example.com,user=zyx'")
    String settings;

    @Override
    public void execute() {
        // /conductor/{conductorID}/collector/{collectorName}        
        // data: settings
        RestHandler.callRest(Main.getEliseAPI("/manager/conductor/"+conductorID+"/collector/"+collectorName), RestHandler.HttpVerb.POST, settings, MediaType.TEXT_PLAIN, null);        
    }

    @Override
    public String getCommandDescription() {
        return "Add a collector plugin to conductor to collect information.";
    }
    
}
