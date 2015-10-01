/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceSubmit implements CommandHandler {

    @Argument(index = 0, required = true, metaVar = "toscaFile", usage = "Path to the local Tosca file to be submitted.")
    File toscaFile;
    
    @Override
    public void execute() {        
        try {
            String xml = FileUtils.readFileToString(toscaFile);
            System.out.println("Submiting tosca file: " + toscaFile);            
            RestHandler.callRest(Main.getSalsaAPI("/services/xml"), RestHandler.HttpVerb.PUT, xml, MediaType.APPLICATION_XML, null);            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
                
    }

    @Override
    public String getCommandDescription() {
        return "Submit a TOSCA to start a deployment.";
    }
    
}
