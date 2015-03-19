package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;

import java.io.File;
import java.io.IOException;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class SystemFunctions {
   
    private final static File envFileGlobal = new File("/etc/environment");

   
    
    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = SystemFunctions.class.getResource("/pioneer-scripts/getEth0IPv4.sh");
        File dest = new File("/tmp/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot create template script file from: " + inputUrl + " to: " + dest.getPath());
        }
        return executeCommand("/bin/bash /tmp/getEth0IPv4.sh", "/tmp", null, null);
    }

    /**
     * Run a command and wait
     *
     * @param cmd The command to run
     * @param workingDir The folder where the command is run
     * @param centerCon A center connection to log to the center. Null = no log
     * @param executeFrom For logging message to the center of where to execute the command.
     * @return
     */
    public static String executeCommand(String cmd, String workingDir, SalsaCenterConnector centerCon, String executeFrom) {
        EngineLogger.logger.debug("Execute command: " + cmd);
        if (centerCon != null) {
            centerCon.logMessage("Execute command from: " + executeFrom + ". Cmd: " + cmd);
        }
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
            EngineLogger.logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
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
                EngineLogger.logger.debug(line);
            }
            p.waitFor();
            System.out.println("Execute Commang output: " + output.toString().trim());

            if (p.exitValue() == 0) {
                EngineLogger.logger.debug("Command exit 0, result: " + output.toString().trim());
                return output.toString().trim();
            } else {
                EngineLogger.logger.debug("Command return non zero code: " + p.exitValue());
                return null;
            }
        } catch (InterruptedException | IOException e1) {
            EngineLogger.logger.error("Error when execute command. Error: " + e1);
        }
        return null;
    }

}
