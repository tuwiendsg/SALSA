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
public class DependencyConfigurationException extends SalsaException{

    @Override
    public int getErrorCode() {
        return SalsaException.ErrorType.CONFIGURATION_PROCESS;
    }

    public DependencyConfigurationException(String node, String dependOn, String detail) {
        super("The configuration of node: " + node +" cannot be done due to the failure of " + dependOn +". The cause could be: " + detail );
    }
    
    
    
}
