/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ReflectionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class SystemFunctions {

    private final static File envFileGlobal = new File("/etc/environment");
    private static final Logger logger = EngineLogger.logger;
    static private String localIP = null;

    public static String getEth0IPAddress() {
        if (localIP != null) {
            logger.debug("Return local IP address:" + localIP);
            return localIP;
        }
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = SystemFunctions.class.getResource("/scripts/getEth0IPv4.sh");
        EngineLogger.logger.debug("Search script: " + inputUrl);
        File dest = new File("/tmp/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot create template script file from: " + inputUrl + " to: " + dest.getPath());
        }
        localIP = executeCommandGetOutput("/bin/bash /tmp/getEth0IPv4.sh", "/tmp", null);
        localIP = localIP.trim();
        if (isIPv4Address(localIP)) {
            logger.debug("The {} is IPv4 !", localIP);
            return localIP;
        } else {
            logger.debug("Cannot get IPv4, the command return: {}", localIP);
            localIP = null;
            return "localhost";
        }
    }

    public static boolean isIPv4Address(String address) {
        if (address.isEmpty()) {
            return false;
        }
        try {
            Object res = InetAddress.getByName(address);
            return (res.getClass().equals(Inet4Address.class));
        } catch (final UnknownHostException ex) {
            logger.error("Cannot get the host IP address. The captured address is: {}. Error: {}", address, ex.getMessage());
            return false;
        }
    }

    public static void executeCommandSimple(String cmd, String workingDir) {
        logger.debug("Running command: {} in folder: {}", cmd, workingDir);
        Process p;
        try {
            String newCmd = "cd " + workingDir + " && " + cmd;
            logger.debug("The whole command to execute: {}", newCmd);
            p = Runtime.getRuntime().exec(newCmd);
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error when run command: {}", cmd, ex);
        }
    }

    public static int executeCommandGetReturnCode(String cmd, String workingDir, String executeFrom) {
        if (workingDir == null) {
            workingDir = "/tmp";
        }
        logger.debug("Execute command: " + cmd + ". Working dir: " + workingDir);
        try {
            String[] splitStr = cmd.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(splitStr);
            pb.directory(new File(workingDir));
            pb = pb.redirectErrorStream(true);  // this is important to redirect the error stream to output stream, prevent blocking with long output
            Map<String, String> env = pb.environment();
            String path = env.get("PATH");
            path = path + File.pathSeparator + "/usr/bin:/usr/sbin";
            logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
            env.put("PATH", path);

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }
            p.waitFor();
            int returnCode = p.exitValue();
            logger.debug("Execute command done: " + cmd + ". Get return code: " + returnCode);
            return returnCode;
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
        }
        return -1;
    }

    /**
     * Run a command and wait
     *
     * @param cmd The command to run
     * @param workingDir The folder where the command is run
     * @param executeFrom For logging message to the center of where to execute the command.
     * @return
     */
    public static String executeCommandGetOutput(String cmd, String workingDir, String executeFrom) {
        logger.debug("Execute command: " + cmd);
        if (workingDir == null) {
            workingDir = "/tmp";
        }
        try {
            String[] splitStr = cmd.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(splitStr);
            pb.directory(new File(workingDir));
            pb = pb.redirectErrorStream(true);  // this is important to redirect the error stream to output stream, prevent blocking with long output
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
            if (lineCount >= 10) {
                logger.debug("... there are alot of more output here which is not shown ! ...");
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

    /**
     * Run a command and wait
     *
     * @param cmd The command to run
     * @param workingDir The folder where the command is run
     * @param executeFrom For logging message to the center of where to execute the command.
     * @return
     */
    public static Process executeCommandAndForget(String cmd, String workingDir, String executeFrom) {
        logger.debug("Execute command: " + cmd);
        if (workingDir == null) {
            workingDir = "/tmp";
        }

        String[] splitStr = cmd.split("\\s+");
        ProcessBuilder pb = new ProcessBuilder(splitStr);
        pb.directory(new File(workingDir));
        pb = pb.redirectErrorStream(true);  // this is important to redirect the error stream to output stream, prevent blocking with long output
        pb.redirectOutput(new File("/tmp/salsa.conductor.log"));
        Map<String, String> env = pb.environment();
        String path = env.get("PATH");
        path = path + File.pathSeparator + "/usr/bin:/usr/sbin";
        logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
        env.put("PATH", path);
        Process p;
        try {
            p = pb.start();
            return p;
        } catch (IOException ex) {
            logger.debug("Cannot run the command: " + cmd);
            return null;
        }

    }

    public static List<String> getEndPoints() throws MalformedObjectNameException,
            NullPointerException, UnknownHostException, AttributeNotFoundException,
            InstanceNotFoundException, MBeanException, ReflectionException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        String hostname = InetAddress.getLocalHost().getHostName();
        InetAddress[] addresses = InetAddress.getAllByName(hostname);
        ArrayList<String> endPoints = new ArrayList<>();
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
     * This command try to get Port which the service is listening to. The port should be in the parameter -httpPort when running, or Tomcat port
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
