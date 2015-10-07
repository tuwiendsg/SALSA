/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class DeviceCapabilities {

    List<DeviceCapability> capabilities = new ArrayList<>();

    public List<DeviceCapability> getCapabilities() {
        return this.capabilities;
    }

    public void setCapabilities(List<DeviceCapability> capabilities) {
        this.capabilities = capabilities;
    }

    public void addCapability(DeviceCapability c) {
        this.capabilities.add(c);
    }
}
