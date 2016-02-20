/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.CapabilityType;

/**
 *
 * @author hungld
 */
public abstract class CloudConnectivity extends Capability {

    String connectivityMode;
    String cloudEndpoint;

    public abstract void changeEndpoint(String newEndpoint);

    public abstract void changeConnectivityMode(String newConnectivityMode);

    public abstract void reconfigureNetwork(String configuration);

    /****************
     * GETER/SETTER *
     ****************/
    public CloudConnectivity() {
        type = CapabilityType.CloudConnectivity;
    }

    public CloudConnectivity(String connectivityMode, String cloudEndpoint) {
        this.connectivityMode = connectivityMode;
        this.cloudEndpoint = cloudEndpoint;
    }

    public String getConnectivityMode() {
        return connectivityMode;
    }

    public void setConnectivityMode(String connectivityMode) {
        this.connectivityMode = connectivityMode;
    }

    public String getCloudEndpoint() {
        return cloudEndpoint;
    }

    public void setCloudEndpoint(String cloudEndpoint) {
        this.cloudEndpoint = cloudEndpoint;
    }

}
