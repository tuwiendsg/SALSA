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
public class WebContainerInfo extends DomainEntity {

    protected String type;
    protected String apiEndpoint;
    
    public enum States{
        stopped, running
    }

    public WebContainerInfo() {
    }

    public WebContainerInfo(String domainID, String name) {
        super(ServiceCategory.TomcatContainer, domainID, name);
        updateStateList(States.values());
    }

}
