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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;

public class SystemFunctions {

    static org.slf4j.Logger logger = PioneerConfiguration.logger;
    private final static File envFileGlobal = new File("/etc/environment");

    // write to a /etc/profile.d and the /etc/environment
    // the format is metric=value;metric2=value2
    public static void writeSystemVariable(String keyAndValue) {
        try {
            String[] pairs=keyAndValue.split(";");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(envFileGlobal.getPath(), true)));
            for (String s:pairs){
                out.println(s +"\n");
            }
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

        String[] splitStr = cmd.split("\\s+");
        ProcessBuilder pb = new ProcessBuilder(splitStr);
        pb.directory(new File(workingDir));
        pb = pb.redirectErrorStream(true);  // this is important to redirect the error stream to output stream, prevent blocking with long output
        Map<String, String> env = pb.environment();
        String path = env.get("PATH");
        path = path + File.pathSeparator + "/usr/bin:/usr/sbin";
        logger.debug("PATH to execute command: " + pb.environment().get("PATH"));
        env.put("PATH", path);
        try {
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

            p.getInputStream().close();

            if (p.exitValue() == 0) {
                logger.debug("Command exit 0, result: " + output.trim());
                return output.trim();
            } else {
                logger.debug("Command return non zero code: " + p.exitValue());
                return null;
            }
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
            e1.printStackTrace();
        }
        return null;
    }

    public static int executeCommandGetReturnCode(String cmd, String workingDir, String executeFrom) {
//        try {            
//            String[] splitStr = cmd.split("\\s+");
//            ManagedProcessBuilder builder = new ManagedProcessBuilder(new File(splitStr[0]));
//            if (splitStr.length>1){
//                for(String s: splitStr){
//                    builder.addArgument(s);
//                }
//            }         
////            ManagedProcessBuilder builder = new ManagedProcessBuilder(cmd);
//            builder.setWorkingDirectory(new File(workingDir));  
//            
//            String path =  builder.getEnvironment().get("PATH");
//            if (path == null){
//                path="/usr/bin:/usr/sbin";
//            } else {
//                path = path + File.pathSeparator + "/usr/bin:/usr/sbin";
//            }
//            builder.getEnvironment().put("PATH", path);
//            ManagedProcess p = builder.build();
//            
//            p.start();
//            p.waitForExit();
//            return p.exitValue();
        return executeCommandGetReturnCode(cmd, workingDir, executeFrom, 0);
//        } catch (ManagedProcessException ex) {
//            logger.error("error when execute command: {}, error: {}, cause: {}", cmd, ex.getMessage(), ex.getCause());
//            
//            ex.printStackTrace();
//            return 1;
//        }
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

//            InputStream is = p.getInputStream();
//            int c;
//            while ((c = is.read()) != -1) {
//                System.out.print((char) c);
//            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }
//            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), System.out);
//            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), System.out);
//
//            errorGobbler.start();
//            outputGobbler.start();
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
                logger.debug(line + "[buffered]");
            }
            
            
//            errorGobbler.join();   // Handle condition where the
//            outputGobbler.join();  // process ends before the threads finish

            logger.debug("Execute command done: " + cmd + ". Get return code: " + returnCode);
            return returnCode;
        } catch (InterruptedException | IOException e1) {
            logger.error("Error when execute command. Error: " + e1);
            e1.printStackTrace();
        } catch (Exception e) {
            logger.error("Unknown error when execute command: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    static class StreamGobbler extends Thread {

        InputStream is;
        PrintStream os;

        StreamGobbler(InputStream is, PrintStream os) {
            this.is = is;
            this.os = os;
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) {
                    os.print((char) c);
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

}
