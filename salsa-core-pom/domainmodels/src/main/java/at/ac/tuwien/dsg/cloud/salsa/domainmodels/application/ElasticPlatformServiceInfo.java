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
public class ElasticPlatformServiceInfo extends DomainEntity{

    protected String apiEndpoint;
    protected String apiType;
    
    public ElasticPlatformServiceInfo() {
    }
    
    public enum States{
        stopped, running
    }

    public ElasticPlatformServiceInfo(String domainID, String name) {
        super(ServiceCategory.ElasticPlatformService, domainID, name);
        updateStateList(States.values());
    }
    
    
}
