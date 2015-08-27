/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.IoT;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

/**
 *
 * @author Duc-Hung LE
 */
public class SensorInfo extends DomainEntity {
    
    protected String localtion;

    public SensorInfo() {
    }
    
    public enum States{
        stopped, activated
    }

    public SensorInfo(String domainID, String name) {
        super(ServiceCategory.Sensor, domainID, name);
        updateStateList(States.values());
    }
}
