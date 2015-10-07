/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.conductor.listener;

import at.ac.tuwien.dsg.cloud.elise.collector.CollectorSettings.ConductorConfiguration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;


/**
 *
 * @author Duc-Hung Le
 */
public class RunCollector {

    private static final Logger logger = ConductorConfiguration.logger;
    private static final String mainFolder = ConductorConfiguration.getExtensionFolder();
    private static final String FILE_JAR_EXT = ".jar";

    public static void RunAllCollector() {
        logger.debug("Running all collector ...");

        String[] folders = listSubFolder(mainFolder);
        logger.debug("Number of child folders: " + folders.length);
        for (String f : folders) {
            String checkingDir = mainFolder + "/" + f;
            logger.debug("Checking folder: " + f);
            File file = new File(checkingDir);

            String[] jars = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            logger.debug("Number of jar file: " + jars.length);
            for (String jar : jars) {
                logger.debug("Runing collector: " + jar);
                try {
                    FileUtils.copyFile(new File(ConductorConfiguration.ELISE_CONFIGURATION_FILE), new File(checkingDir + "/elise.conf"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //logger.debug(executeCommand3("java -jar " + checkingDir + "/" + jar, checkingDir));
                String cmd = "java -jar " + checkingDir + "/" + jar;
                logger.debug("Executing command: " + cmd + ", in directory: " + checkingDir);
                executeCommand3(cmd, checkingDir);
            }
        }
    }

    private static String[] listSubFolder(String mainFolder) {
        File file = new File(mainFolder);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        return directories;
    }

    public static void executeCommand2(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("echo \"" + cmd + "\" > /tmp/command.sh");
            p.waitFor();
            p = Runtime.getRuntime().exec("bash /tmp/command.sh > output.log");
            p.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static String executeCommand3(String command, String workingDir) {
        StringBuffer output = new StringBuffer();
        String[] env = {"/bin", "/usr/bin", "/opt/java/bin"};
        try {
            Process p = Runtime.getRuntime().exec(command, env, new File(workingDir));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }
            while ((line = reader1.readLine()) != null) {
                logger.debug(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static void executeCommandNohup(String cmd, String workingDir) {
        String[] env = {"/bin", "/usr/bin", "/opt/java/bin"};
        try {
            Runtime.getRuntime().exec(cmd, env, new File(workingDir));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
