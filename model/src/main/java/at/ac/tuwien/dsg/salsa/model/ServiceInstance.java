/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;

/**
 *
 * @author hungld
 */
public class ServiceInstance {

    Long id;

    String uuid; // the unique access of the instance
    int index;// the index under service unit
    String serviceUnitUuid; // uuid of the service unit
    GlobalIdentification identification; // composition of identification
    ConfigurationState state;
    int hostedInstanceIndex;

    public String getServiceUnitUuid() {
        return serviceUnitUuid;
    }

    public void setServiceUnitUuid(String serviceUnitUuid) {
        this.serviceUnitUuid = serviceUnitUuid;
    }

    public GlobalIdentification getIdentification() {
        return identification;
    }

    public void setIdentification(GlobalIdentification identification) {
        this.identification = identification;
    }

    public ConfigurationState getState() {
        return state;
    }

    public void setState(ConfigurationState state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getHostedInstanceIndex() {
        return hostedInstanceIndex;
    }

    public void setHostedInstanceIndex(int hostedInstanceIndex) {
        this.hostedInstanceIndex = hostedInstanceIndex;
    }

}
