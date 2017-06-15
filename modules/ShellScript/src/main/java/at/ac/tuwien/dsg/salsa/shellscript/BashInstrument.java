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
package at.ac.tuwien.dsg.salsa.shellscript;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.ShellScriptParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;

import org.slf4j.Logger;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class BashInstrument implements ConfigurationModule {

    Logger logger = LoggerFactory.getLogger("BashModule");

    @Override
    public SalsaConfigureResult configureArtifact(SalsaConfigureTask configInfo, Map<String, String> parameters) {
        String workingDir = parameters.get("workingdir");
        logger.debug("Start configure via BASH script ...");
        String cmd = "/bin/bash " + configInfo.getParameters(ShellScriptParameters.runByMe);
        logger.debug("Running command: " + cmd);

        logger.debug("Working dir: " + workingDir);
        int returnCode = SystemFunctions.executeCommandGetReturnCode(cmd, workingDir, configInfo.getActionID());
        logger.debug("Command is done, return code is : " + returnCode);
        if (returnCode == 0) {
            return new SalsaConfigureResult(configInfo.getActionID(), ConfigurationState.SUCCESSFUL, returnCode, "Configure script DONE: " + configInfo.getParameters(ShellScriptParameters.runByMe));
        } else {
            return new SalsaConfigureResult(configInfo.getActionID(), ConfigurationState.ERROR, returnCode, "Configure script FAILED: " + configInfo.getParameters(ShellScriptParameters.runByMe));
        }
    }

    @Override
    public String getStatus(SalsaConfigureTask configInfo) {
        return "Unknown";
    }

    @Override
    public String getName() {
        return "bash";
    }

}
