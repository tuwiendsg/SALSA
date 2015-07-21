/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.exceptions;

import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;

/**
 *
 * @author hungld
 */
public class ArtifactDeploymentException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CONFIGURATION_PROCESS;
    }

    public enum ArtifactDeploymentError {

        DOWNLOAD_ARTIFACT_FAILURE,
        CONFIGURATION_SCRIPT_FAILURE,
        CAPABILITY_STRING_TRANSTERRING_ERROR,
        TIMEOUT,
        UNKNOWN
    }

    public ArtifactDeploymentException(String serviceID, String unitID, int instanceID, ArtifactDeploymentError reason, String extra) {
        super("The service instance with id [" + serviceID + "/" + unitID + "/" + instanceID + "] is failed in deploying due to [" + reason + "]. More info: " + extra);
    }

}
