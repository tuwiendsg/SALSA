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
public class IllegalConfigurationAPICallException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CLIENT_BAD_REQUEST;
    }

    public IllegalConfigurationAPICallException() {
    }

    public IllegalConfigurationAPICallException(String cause) {
        super("Illegal call for the API. Because: " + cause);
    }

    public IllegalConfigurationAPICallException(String cause, Exception e) {
        super("Illegal call for the API. Because: " + cause, e);
    }

    public IllegalConfigurationAPICallException(Exception e) {
        super(e);
    }

}
