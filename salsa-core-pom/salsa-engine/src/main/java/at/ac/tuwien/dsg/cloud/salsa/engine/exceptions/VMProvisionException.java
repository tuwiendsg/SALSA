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
public class VMProvisionException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CONFIGURATION_PROCESS;
    }

    public enum VMProvisionError {

        QUOTA_LIMITED,
        CLOUD_FAILURE,
        TIMEOUT,
        UNKNOWN
    }

    public VMProvisionException(String cloud, String serviceID, String unitID, int instanceID, VMProvisionError reason, String extra) {
        super("The VM with id " + serviceID + "/" + unitID + "/" + instanceID + " on the cloud [" + cloud + "] is failed in provisioning due to [" + reason + "]. More info: " + extra);
    }

    public VMProvisionException(VMProvisionError reason, String extra) {
        super("The VM is failed to created. Reason: " + reason + ". " + extra);
    }

}
