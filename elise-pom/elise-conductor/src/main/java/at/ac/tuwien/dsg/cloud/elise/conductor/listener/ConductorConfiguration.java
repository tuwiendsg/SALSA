/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.conductor.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Duc-Hung LE
 */
public class ConductorConfiguration {

    public static Logger logger = LoggerFactory.getLogger("EliseConductor");

    public final static String ELISE_HOME = System.getProperty("user.dir");
    public final static String DATA_BASE_STORAGE = ELISE_HOME + "/comotElise.db";
    public final static String ELISE_CONFIGURATION_FILE = ELISE_HOME + "/elise.conf";
    
    public final static String BROKER = getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");

    private static final String conductorID = UUID.randomUUID().toString();

    public static String getConductorID() {
        return conductorID;
    }
    
    
    public static String getGenericParameter(String key, String theDefault) {
        Properties prop = new Properties();
        InputStream input;
        File myFile = new File(ELISE_CONFIGURATION_FILE);

        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            input = new FileInputStream(ELISE_CONFIGURATION_FILE);
            // load a properties file
            prop.load(input);
            String param = prop.getProperty(key);
            if (param != null) {   // just return default MQTT
                return param;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return theDefault;
    }
    
    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = ConductorConfiguration.class.getResource("/scripts/getEth0IPv4.sh");
        logger.debug("Search script: " + inputUrl);
        File dest = new File("/tmp/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            logger.error("Cannot create template script file from: " + inputUrl + " to: " + dest.getPath());
        }
        return executeCommand("/bin/bash /tmp/getEth0IPv4.sh", "/tmp");
    }
    
    public static String executeCommand(String cmd, String workingDir) {
        logger.debug("Execute command: " + cmd);
        if (workingDir == null) {
            workingDir = "/tmp";
        }
        try {
            String[] splitStr = cmd.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(splitStr);
            pb.directory(new File(workingDir));
            Map<String, String> env = pb.environment();
            String path = env.get("PATH");
            path = path + File.pathSeparator + "/usr/bin:/usr/sbin";
            logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
            env.put("PATH", path);

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuffer output = new StringBuffer();
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (lineCount < 10) {  // only get 10 lines to prevent the overflow
                    output.append(line);
                }
                lineCount += 1;
                logger.debug(line);
            }
            p.waitFor();
            System.out.println("Execute Commang output: " + output.toString().trim());

            if (p.exitValue() == 0) {
                logger.debug("Command exit 0, result: " + output.toString().trim());
                return output.toString().trim();
            } else {
                logger.debug("Command return non zero code: " + p.exitValue());
                return null;
            }
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
        }
        return null;
    }

}
