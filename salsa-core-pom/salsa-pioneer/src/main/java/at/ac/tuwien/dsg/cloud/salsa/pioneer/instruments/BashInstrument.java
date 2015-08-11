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

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;


import org.slf4j.Logger;

public class BashInstrument implements ArtifactConfigurationInterface {

    static Logger logger = PioneerConfiguration.logger;

    /**
     * This match with the "sh" artifact type, which will exit after deployment So, SALSA will WAIT until the process finishes
     */
    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {
        // get the first sh script in the artifact list
        logger.debug("Start configure via BASH script ...");        
        String cmd = "/bin/bash " + configInfo.getRunByMe();
        logger.debug("Running command: " + cmd);
        String workingDir = PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance());
        logger.debug("Working dir: " + workingDir);
        int returnCode = SystemFunctions.executeCommandGetReturnCode(cmd, workingDir, configInfo.getActionID());
        logger.debug("Command is done, return code is : " + returnCode);
        if (returnCode == 0){
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, returnCode, "Configure script DONE: " + configInfo.getRunByMe());
        } else {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, returnCode, "Configure script FAILED: " + configInfo.getRunByMe());
        }
    }

    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        return "Unknown";
    }

}
