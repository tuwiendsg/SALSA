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
package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;

/**
 * This is the same with BASH instrument, but for running the process which does not exit
 *
 * @author Duc-Hung Le
 */
public class BashContinuousInstrument extends BashContinuousManagement implements ArtifactConfigurationInterface {

    static Logger logger = PioneerConfiguration.logger;

    /**
     * This match with the "shcont" artifact type, do NOT exist
     */
    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {
        String workingDir = PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance());
        String cmd = "/bin/bash " + configInfo.getRunByMe();

        logger.debug("Configuring by BASH script: " + cmd);
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
            addInstanceProcess(configInfo.getActionID(), p);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuffer output = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                logger.debug(line);
            }
            System.out.println("Execute Command output: " + output.toString().trim());

            logger.debug("The configuration script is RUNNING. Action ID: " + configInfo.getActionID() + ", runByMe: " + configInfo.getRunByMe());
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, p.exitValue(), "Configure script DONE: " + configInfo.getRunByMe());
        } catch (IOException e1) {
            e1.printStackTrace();
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, 0, "Configure script get an IOException: " + configInfo.getRunByMe());
        }

    }

    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        Process p = processMapping.get(configInfo.getActionID());
        if (p == null) {
            return "Process is stopped";
        } else {
            return "Process is running";
        }
    }

}
