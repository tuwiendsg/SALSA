/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;
import static at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashInstrument.logger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;

/**
 *
 * @author Duc-Hung LE
 */
public class BinaryExecutionInstrument implements ArtifactConfigurationInterface {

    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {        
        String cmd = configInfo.getRunByMe();
        logger.debug("Start executing the binary command: " + cmd);        
        String workingDir = PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance());
        logger.debug("Working dir: " + workingDir);
        int returnCode = SystemFunctions.executeCommandGetReturnCode(cmd, workingDir, configInfo.getActionID());
        logger.debug("Command is done, return code is : " + returnCode);
        if (returnCode == 0) {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, returnCode,  "Configure script DONE: " + configInfo.getRunByMe());
        } else {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, returnCode,   "Configure script FAILED: " + configInfo.getRunByMe());
        }
    }

    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        return "Unknown";
    }

}
