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
public class AppDescriptionException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CLIENT_BAD_REQUEST;
    }

    public AppDescriptionException(String element, String error) {
        super("The application is bad described. Please check element: " + element + ", for the error: " + error);
    }

    public AppDescriptionException(String element, String error, Exception e) {
        super("The application is bad described. Please check element: " + element + ", for the error: " + error, e);
    }
}
