/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.configurationAPI;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceSubmit implements CommandHandler {

    @Argument(index = 0, required = true, metaVar = "serviceName", usage = "The name of the service.")
    File serviceName;

    @Argument(index = 1, required = true, metaVar = "salsaFile", usage = "Path to the file to be submitted.")
    File salsaFile;

    @Override
    public void execute() {
        Process p;
        if (salsaFile.exists() && !salsaFile.isDirectory()) {
            try {
                String cmd = "curl -i -X POST -H \"Content-Type: multipart/form-data\" -F \"file=@" + salsaFile + "\" " + Main.getSalsaAPI("/services/" + serviceName);
                System.out.println("Running command: " + cmd);
                String[] cmds = new String[] {"curl", "-i", "-X", "POST", "-H", "Content-Type: multipart/form-data", "-F", "file=@" + salsaFile, Main.getSalsaAPI("/services/" + serviceName)};
                p = Runtime.getRuntime().exec(cmds);

                p.waitFor();

                BufferedReader reader1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader1.readLine()) != null) {
                    System.out.println(line);
                }
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = reader2.readLine()) != null) {
                    System.out.println(line);
                }
                System.out.println("Upload file done, now initiate service.");
                RestHandler.callRest(Main.getSalsaAPI("/services/" + serviceName), RestHandler.HttpVerb.PUT, null, null, null);
                System.out.println("Service initiation done.");
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public String getCommandDescription() {
        return "Submit a SalsaFile or SalsaPack to start a deployment.";
    }

}
