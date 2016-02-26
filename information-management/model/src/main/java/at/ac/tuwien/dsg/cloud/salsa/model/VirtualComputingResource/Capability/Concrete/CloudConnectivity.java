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
public class CloudConnectivity extends Capability {

    // note the "name" can be used to define connection mode: 3G, 4G, WIFI, 
    // the IP and port of the gateway/router that the resource link to
    String defaultGateway;

    // the endpoint of the cloud service it connect to
    String cloudEndpoint;

    String protocol;

    ControlPoint controlChangeProtocol = null;

    /**
     * **************
     * GETER/SETTER * **************
     */
    public CloudConnectivity() {
        type = CapabilityType.CloudConnectivity;
    }

    public CloudConnectivity(String resourceID, String name, String description) {
        super( resourceID, name, CapabilityType.CloudConnectivity, description);
    }

    public CloudConnectivity(String resourceID, String name, String description, String defautGateway, String cloudEndpoint) {
        super(resourceID, name, CapabilityType.CloudConnectivity, description);
        this.cloudEndpoint = cloudEndpoint;
        this.defaultGateway = defautGateway;
    }

    public String getCloudEndpoint() {
        return cloudEndpoint;
    }

    public void setCloudEndpoint(String cloudEndpoint) {
        this.cloudEndpoint = cloudEndpoint;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public ControlPoint getControlChangeProtocol() {
        return controlChangeProtocol;
    }

    public void setControlChangeProtocol(ControlPoint controlChangeProtocol) {
        this.controlChangeProtocol = controlChangeProtocol;
    }

}
