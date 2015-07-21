/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.exception;

/**
 *
 * @author hungld
 */
public class ServicedataProcessingException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.ENGINE_INTERNAL;
    }

    public ServicedataProcessingException(String serviceID) {
        super("Cannot process service data file for service: " + serviceID + ". Cause is unknown, maybe file is not found.");
    }

    public ServicedataProcessingException(String serviceID, Exception e) {
        super("Cannot process service data file for service: " + serviceID, e);
    }
}
