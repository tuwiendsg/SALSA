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
public class EngineMisconfiguredException extends SalsaException {

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.ENGINE_INTERNAL;
    }

    public EngineMisconfiguredException(String configFile, String variableName) {
        super("An error occured due to a misconfiguration of the file: " + configFile + ", error may occur in: " + variableName);
    }

}
