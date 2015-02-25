package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemFunctions {

    private final static File configFile = new File("/etc/profile.d/salsa-relationship-values.sh");
    private final static File envFileGlobal = new File("/etc/environment");

    static {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                PioneerLogger.logger.error("Could not create config file. Error: " + e);
            }
        }
    }

    // write to a /etc/profile.d and the /etc/environment
    public static void writeSystemVariable(String key, String value) {
        try {
            PioneerLogger.logger.debug("WRITE ENV: " + key + " -- " + value);
            String data = "export " + key + "=" + value + "\n";
            PioneerLogger.logger.debug(data);
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFile.getPath(), true)));
            out.println(data);
            out.flush();
            out.close();

            String data1 = key + "=" + value + "\n";
            out = new PrintWriter(new BufferedWriter(new FileWriter(envFileGlobal.getPath(), true)));
            out.println(data1);
            out.flush();
            out.close();

        } catch (IOException e) {
            PioneerLogger.logger.error("Could not write in config file. Error: " + e);
        }

    }

    public static String getLocalIpAddress() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return ip;
        } catch (Exception e) {
            PioneerLogger.logger.error("Cannot get the local IP adress");
        }
        return "";
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
        PioneerLogger.logger.debug("Execute command: " + cmd);
        if (centerCon != null) {
            centerCon.logMessage("Execute command from: " + executeFrom + ". Cmd: " + cmd);
        }
        try {
            Process p;
            if (workingDir == null) {
                p = Runtime.getRuntime().exec(cmd);
            } else {
                p = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            StringBuffer output = new StringBuffer();
            int lineCount=0;
            while ((line = reader.readLine()) != null) {
                if (lineCount<10){  // only get 10 lines to prevent the overflow
                    output.append(line);
                }
                lineCount+=1;
                PioneerLogger.logger.debug(line);
            }
            p.waitFor();
            System.out.println("Execute Commang output: " + output.toString().trim());
            return output.toString().trim();
        } catch (InterruptedException | IOException e1) {
            PioneerLogger.logger.error("Error when execute command. Error: " + e1);
        }
        return null;
    }

}
