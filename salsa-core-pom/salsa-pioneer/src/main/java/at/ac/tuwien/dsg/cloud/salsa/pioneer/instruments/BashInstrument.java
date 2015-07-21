package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.items.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import org.apache.commons.io.FilenameUtils;


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
