package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;

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

}
