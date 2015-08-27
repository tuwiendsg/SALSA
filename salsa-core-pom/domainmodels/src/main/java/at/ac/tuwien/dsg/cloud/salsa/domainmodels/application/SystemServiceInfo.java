/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.application;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

/**
 *
 * @author Duc-Hung LE
 */
public class SystemServiceInfo extends DomainEntity {

    protected long pID;
    protected String status;
    
    public enum States{
        stopped, running
    }

    public SystemServiceInfo() {
    }

    public SystemServiceInfo(String domainID, String name) {
        super(ServiceCategory.SystemService, domainID, name);
        updateStateList(States.values());
    }

    public long getpID() {
        return pID;
    }

    public void setpID(long pID) {
        this.pID = pID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
