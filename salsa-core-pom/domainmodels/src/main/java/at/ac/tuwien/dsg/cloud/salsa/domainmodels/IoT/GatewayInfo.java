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
    String location;    
    String ip;

    public GatewayInfo() {
    }
    
    public enum States{
        stopped, running
    }

    public GatewayInfo(String domainID, String name) {
        super(ServiceCategory.Gateway, domainID, name);
        updateStateList(States.values());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    

    
}
