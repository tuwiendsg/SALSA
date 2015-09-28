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
package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;

public class SystemFunctions {

    static org.slf4j.Logger logger = PioneerConfiguration.logger;
    private final static File envFileGlobal = new File("/etc/environment");

    // write to a /etc/profile.d and the /etc/environment
    public static void writeSystemVariable(String key, String value) {
        try {
            String data1 = key + "=" + value + "\n";
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(envFileGlobal.getPath(), true)));
            out.println(data1);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("Could not write in config file. Error: " + e);
        }
    }

    public static void writeSystemVariable(String keyAndValue) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(envFileGlobal.getPath(), true)));
            out.println(keyAndValue);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("Could not write in config file. Error: " + e);
        }
    }

    public static String getEth0IPAddress() {
        // copy the getEth0IPv4 to /tmp and execute it, return the value        
        URL inputUrl = SystemFunctions.class.getResource("/scripts/getEth0IPv4.sh");
        File dest = new File("/tmp/getEth0IPv4.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return executeCommandGetFirstLineOutput("/bin/bash /tmp/getEth0IPv4.sh", "/tmp", null);
    }

    /**
     * Run a command and wait, return the first line of the output
     *
     * @param cmd The command to run
     * @param workingDir The folder where the command is run
     * @param executeFrom For logging message to the center of where to execute the command.
     * @return
     */
    public static String executeCommandGetFirstLineOutput(String cmd, String workingDir, String executeFrom) {
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
            String output = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }

            p.waitFor();

            while ((line = reader.readLine()) != null) {
                logger.debug(line + "[buffered]");
            }

            logger.debug("Execute Command output: " + output.trim());

            if (p.exitValue() == 0) {
                logger.debug("Command exit 0, result: " + output.trim());
                return output.trim();
            } else {
                logger.debug("Command return non zero code: " + p.exitValue());
                return null;
            }
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
        }
        return null;
    }

    public static int executeCommandGetReturnCode(String cmd, String workingDir, String executeFrom) {
        return executeCommandGetReturnCode(cmd, workingDir, executeFrom, 0);
    }

    public static int executeCommandGetReturnCode(String cmd, String workingDir, String executeFrom, long timeout) {
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
            //logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
            env.put("PATH", path);

            Process p = pb.start();
            logger.debug("Process started: " + cmd);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }

            int returnCode = -1;
            if (timeout == 0) {
                p.waitFor();
                returnCode = p.exitValue();
            } else {
                boolean finished = p.waitFor(timeout, TimeUnit.SECONDS);
                if (finished) {
                    returnCode = p.exitValue();
                } else {
                    logger.error("Command " + cmd + " is blocked and interrupted after " + timeout + " seconds");
                }
            }

            while ((line = reader.readLine()) != null) {
                logger.debug(line + "[buffered]" );
            }

            logger.debug("Execute command done: " + cmd + ". Get return code: " + returnCode);
            return returnCode;
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
        } catch (Exception e) {
            logger.error("Unknown error when execute command: " + e.getMessage());
        }
        return -1;
    }

}
