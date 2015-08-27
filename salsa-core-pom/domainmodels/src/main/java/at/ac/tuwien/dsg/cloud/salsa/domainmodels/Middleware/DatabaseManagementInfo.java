/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.Middleware;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

/**
 *
 * @author Duc-Hung LE
 */
public class DatabaseManagementInfo extends DomainEntity {
    
    public enum States{
        stopped, running
    }

    public DatabaseManagementInfo() {
    }

    public DatabaseManagementInfo(String domainID, String name) {
        super(ServiceCategory.DatabaseManagement, domainID, name);
        updateStateList(States.values());
    }

}
