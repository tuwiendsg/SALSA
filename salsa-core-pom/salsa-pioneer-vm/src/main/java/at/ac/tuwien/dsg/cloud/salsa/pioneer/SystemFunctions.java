package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator.DockerConfigurator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

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

    private static String getLocalIpAddress() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return ip;
        } catch (Exception e) {
            PioneerLogger.logger.error("Cannot get the local IP adress");
        }
        return "";
    }

    @Deprecated
    private static String getEth0IPAddress_not_work() {
        return executeCommand("hostname -i", null, null, null);
    }
    
    @Deprecated
    public static String getEth0IPAddress_failToUse() {
        try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                            String ipAddress=inetAddress.getHostAddress().toString();
                            PioneerLogger.logger.debug("Getting the IP Address (quite hard to do): " + ipAddress);
                            return ipAddress;
                        }
                    }
                }
            } catch (SocketException ex) {
                
            }
        return null;
    }
    
    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = SystemFunctions.class.getResource("/pioneer-scripts/getEth0IPv4.sh");
        File dest = new File("/tmp/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            Logger.getLogger(DockerConfigurator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return executeCommand("/tmp/getEth0IPv4.sh", "/tmp", null, null);
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
            PioneerLogger.logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
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
                PioneerLogger.logger.debug(line);
            }
            p.waitFor();
            System.out.println("Execute Commang output: " + output.toString().trim());

            if (p.exitValue() == 0) {
                PioneerLogger.logger.debug("Command exit 0, result: " + output.toString().trim());
                return output.toString().trim();
            } else {
                PioneerLogger.logger.debug("Command return non zero code: " + p.exitValue());
                return null;
            }
        } catch (InterruptedException | IOException e1) {
            PioneerLogger.logger.error("Error when execute command. Error: " + e1);
        }
        return null;
    }

}
