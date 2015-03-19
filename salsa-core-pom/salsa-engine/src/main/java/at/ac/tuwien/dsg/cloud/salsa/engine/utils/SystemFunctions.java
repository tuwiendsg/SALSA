package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.ToscaEnricher;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ReflectionException;
import org.apache.commons.io.FileUtils;

public class SystemFunctions {
    private final static File envFileGlobal = new File("/etc/environment");

    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = SystemFunctions.class.getResource("/scripts/getEth0IPv4.sh");
        EngineLogger.logger.debug("Search script: " + inputUrl);
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
     * @return the output of the command
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

    public static List<String> getEndPoints() throws MalformedObjectNameException,
            NullPointerException, UnknownHostException, AttributeNotFoundException,
            InstanceNotFoundException, MBeanException, ReflectionException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        String hostname = InetAddress.getLocalHost().getHostName();
        InetAddress[] addresses = InetAddress.getAllByName(hostname);
        ArrayList<String> endPoints = new ArrayList<String>();
        for (Iterator<ObjectName> i = objs.iterator(); i.hasNext();) {
            ObjectName obj = i.next();
            String scheme = mbs.getAttribute(obj, "scheme").toString();
            String port = obj.getKeyProperty("port");
            for (InetAddress addr : addresses) {
                String host = addr.getHostAddress();
                String ep = scheme + "://" + host + ":" + port;
                endPoints.add(ep);
            }
        }
        return endPoints;
    }

    /**
     * This command try to get Port which the service is listening to. 
     * The port should be in the parameter -httpPort when running, or Tomcat port
     *
     * @return
     */
    public static String getPort() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            for (ObjectName obj : objs) {
                String port = obj.getKeyProperty("port");
                return port;
            }
        } catch (MalformedObjectNameException e) {
            EngineLogger.logger.error("Cannot get listening port of salsa-engine service. return 8080 as default. Error: " + e.toString());            
        }
        EngineLogger.logger.error("Cannot find listening port of salsa-engine service. return 8080 as default");
        return "8080";
    }

}
