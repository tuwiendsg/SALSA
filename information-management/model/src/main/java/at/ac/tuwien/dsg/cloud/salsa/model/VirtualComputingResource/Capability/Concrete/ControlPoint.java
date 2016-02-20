/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.CapabilityType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungld
 */
public abstract class ControlPoint extends Capability{

    Map<String, Object> controlStates;

    public abstract void updateState(String controlState, Object newState);

    
    /****************
     * GETER/SETTER *
     ****************/
    
    public ControlPoint() {
        type = CapabilityType.ControlPointManagement;
    }

    public Map<String, Object> getControlStates() {
        if (controlStates==null){
            controlStates = new HashMap<>();
        }
        return controlStates;
    }

    public void setControlStates(Map<String, Object> controlStates) {
        this.controlStates = controlStates;
    }
    
    
}
