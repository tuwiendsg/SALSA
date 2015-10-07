/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

/**
 *
 * @author hungld
 */
public class DeviceCapability {

    String capability;

    public DeviceCapability(String capability) {
        this.capability = capability;
    }

    public DeviceCapability() {
    }

    public String getCapability() {
        return this.capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
