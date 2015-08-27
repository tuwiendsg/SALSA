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
public class GatewayInfo extends DomainEntity {

    public GatewayInfo() {
    }
    
    public enum States{
        stopped, running
    }

    public GatewayInfo(String domainID, String name) {
        super(ServiceCategory.Gateway, domainID, name);
        updateStateList(States.values());
    }

}
