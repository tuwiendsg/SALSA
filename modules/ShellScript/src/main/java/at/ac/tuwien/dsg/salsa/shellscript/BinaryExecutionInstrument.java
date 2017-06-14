/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.shellscript;

import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.ShellScriptParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class BinaryExecutionInstrument implements ConfigurationModule {

    Logger logger = LoggerFactory.getLogger("BinaryExecution");

    @Override
    public SalsaConfigureResult configureArtifact(SalsaConfigureTask configInfo, Map<String, String> parameters) {
        String workingDir = parameters.get("workingdir");
        String cmd = configInfo.getParameters(ShellScriptParameters.runByMe);
        logger.debug("Start executing the binary command: " + cmd);

        logger.debug("Working dir: " + workingDir);
        int returnCode = SystemFunctions.executeCommandGetReturnCode(cmd, workingDir, configInfo.getActionID());
        logger.debug("Command is done, return code is : " + returnCode);
        if (returnCode == 0) {
            return new SalsaConfigureResult(configInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, returnCode, "Configure script DONE: " + configInfo.getParameters(ShellScriptParameters.runByMe));
        } else {
            return new SalsaConfigureResult(configInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.ERROR, returnCode, "Configure script FAILED: " + configInfo.getParameters(ShellScriptParameters.runByMe));
        }
    }

    @Override
    public String getStatus(SalsaConfigureTask configInfo) {
        return "Unknown";
    }

    @Override
    public String getName() {
        return "BinaryExecution";
    }

}
