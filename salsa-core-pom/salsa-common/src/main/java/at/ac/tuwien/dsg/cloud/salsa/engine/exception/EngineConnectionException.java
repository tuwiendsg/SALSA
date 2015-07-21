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
public class EngineConnectionException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.ENGINE_INTERNAL;
    }

    public EngineConnectionException(String communicationEndpoint) {
        super("Cannot connect to: " + communicationEndpoint + ". Maybe a misconfiguration in salsa.engine.properties caused this.");
    }

}
