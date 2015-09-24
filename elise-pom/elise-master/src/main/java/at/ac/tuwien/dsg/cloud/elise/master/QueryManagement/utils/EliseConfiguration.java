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
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliseConfiguration {

    // the case of using embbeded DB
    public final static String CURRENT_DIR = System.getProperty("user.dir");
    public final static String DATA_BASE_STORAGE = CURRENT_DIR + "/comotElise.db";
    public final static String SALSA_CONFIGURATION_FILE = CURRENT_DIR + "/salsa.engine.properties";
    public final static String ELISE_CONFIGURATION_FILE = CURRENT_DIR + "/elise.conf";
    public final static String IDENTIFICATION_MAPPING_FILE = CURRENT_DIR + "/identification.json";
    // in the case of using separate DB
//    public final static String DATA_BASE_REMOTE_ENDPOINT = "http://localhost:7474/db/data";       
    public static Logger logger = LoggerFactory.getLogger("Elise");
    public static String ELISE_ID = null;

    static {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            // WRITE NEEDED INFO INTO ELISE CONFIGURATION FILE
            output = new FileOutputStream(ELISE_CONFIGURATION_FILE);
            prop.setProperty("ELISE_PORT", getPort());
            prop.setProperty("ELISE_IP", getSALSA_CENTER_IP());
            prop.setProperty("BROKER", getBroker());
            prop.setProperty("BROKER_TYPE", getBrokerType());
            prop.store(output, null);            
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    // NOTE: THIS GET PARAMETER IN SALSA CONFIGURATION FILE
    public static String getGenericParameter(String key, String theDefault) {
        Properties prop = new Properties();
        InputStream input;
        File myFile = new File(SALSA_CONFIGURATION_FILE);

        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            input = new FileInputStream(SALSA_CONFIGURATION_FILE);
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

    public static String getRESTEndpoint() {
        return "http://" + getSALSA_CENTER_IP() + ":" + getPort() + "/salsa-engine/rest/elise";
    }

    public static String getRESTEndpointLocal() {
        return "http://localhost:" + getPort() + "/salsa-engine/rest/elise";
    }

    public static String getEliseID() {
        if (ELISE_ID == null) {
            ELISE_ID = getEth0IPAddress() + ":" + getPort();
        }
        return ELISE_ID;
    }

    private static String getSALSA_CENTER_IP() {
        return getGenericParameter("SALSA_CENTER_IP", getEth0IPAddress());
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
            logger.error("Cannot get listening port of salsa-engine service. return 8080 as default. Error: " + e.toString());
        }
        logger.error("Cannot find listening port of salsa-engine service. return 8080 as default");
        return "8080";
    }

    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = EliseConfiguration.class.getResource("/scripts/getEth0IPv4.sh");
        logger.debug("Search script: " + inputUrl);
        File dest = new File(CURRENT_DIR + "/scripts/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            logger.error("Cannot create template script file from: " + inputUrl + " to: " + dest.getPath());
        }
        return executeCommand("/bin/bash " + CURRENT_DIR + "/scripts/getEth0IPv4.sh", CURRENT_DIR);
    }

    /**
     * Run a command and wait
     *
     * @param cmd The command to run
     * @param workingDir The folder where the command is run
     * @return the output of the command
     */
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
            System.out.println("Execute Command output: " + output.toString().trim());

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

    public static String getBroker() {
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");
    }

    public static String getBrokerType() {
        return getGenericParameter("BROKER_TYPE", "mqtt");
    }
}
