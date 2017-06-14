/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.modules.plainmachine;

import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class LocalMachineConfigurator implements ConfigurationModule {

    Logger logger = LoggerFactory.getLogger("salsa");

    @Override
    public SalsaConfigureResult configureArtifact(SalsaConfigureTask configInfo, Map<String, String> parameters) {
        String userData = parameters.get("userData");

        logger.debug("Trying to run command");
        try {
            String tmp_file = "/tmp/" + UUID.randomUUID().toString();
            logger.debug("Command file name: " + tmp_file);
            File f = new File(tmp_file);
            PrintWriter out = new PrintWriter(f);
            out.print(userData);
            out.flush();
            out.close();

            runLocalhostCommandNoWaitingFor("/bin/bash " + tmp_file);

            //f.delete();
        } catch (FileNotFoundException e) {
            logger.debug("Error when configuring pioneer on localhost");
        }
        String id = "localhost-" + UUID.randomUUID().toString();
        return new SalsaConfigureResult(configInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "Pioneer is started in localhost");
    }

    @Override
    public String getStatus(SalsaConfigureTask configInfo) {
        logger.debug("Get information for localhost");
        return "localhost instance";
    }

    private String runLocalhostCommand(String command) {
        Process p;
        String re = "";
        try {
            logger.debug("Executing command on localhost: " + command);
            p = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while (line != null) {
                line = reader.readLine();
                re += line + "\n";
            }
            p.waitFor();
            logger.debug("Exit value : " + p.exitValue());

        } catch (Exception e) {
            logger.error(e.toString());
        }
        return re;
    }

    private void runLocalhostCommandNoWaitingFor(String command) {
        try {
            logger.debug("Executing command on localhost without wating: " + command);
            Runtime.getRuntime().exec(command);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @Override
    public String getName() {
        return "localmachine";
    }

}
