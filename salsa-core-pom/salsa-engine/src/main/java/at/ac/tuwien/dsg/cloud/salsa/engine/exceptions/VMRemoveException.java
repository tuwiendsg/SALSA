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
public class VMRemoveException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CONFIGURATION_PROCESS;
    }

    public enum Cause {
        VM_DATA_NOT_FOUND,
        CLOUD_FAILURE,
        TIMEOUT
    }

    public VMRemoveException(Cause cause) {
        super("Cannot remove the VM because: " + cause);
    }

    public VMRemoveException(String vmID, Cause cause) {
        super("Cannot remove the VM with ID: " + vmID + " because: " + cause);
    }
}
