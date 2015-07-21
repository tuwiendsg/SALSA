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
public class PioneerManagementException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.ENGINE_INTERNAL;
    }

    public enum Reason {

        PIONEER_NOT_REGISTERED
    }

    public PioneerManagementException(Reason reason, String info) {
        super("Error when working with Pioneer. Reason: " + reason + ". " + info);
    }
}
