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
public abstract class ExecutionEnvironment extends Capability {

    public static enum EnvironmentType {
        JRE, Python, apache2
    }

    EnvironmentType env;
    Map<String, String> attributes;

    public abstract void deploy(String artifactRef);

    public abstract void start(String appRef);

    public abstract void stop(String appRef);

    public abstract void reconfigure(String appRef);

    /**
     * **************
     * GETER/SETTER * **************
     */
    public ExecutionEnvironment() {
        type = CapabilityType.ExecutionEnvironment;
    }

    public ExecutionEnvironment(EnvironmentType env) {
        this.env = env;
    }

    public EnvironmentType getEnv() {
        return env;
    }

    public void setEnv(EnvironmentType env) {
        this.env = env;
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
