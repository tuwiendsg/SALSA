/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author hungld
 */
public class OpenIoTSensor {

    DeviceProperties DeviceProps;
    Asset asset;

    String creationTime;
    boolean isConcentrator;
    SensorModel model;
    String name;
    String registrationTime;
    String status;
    List<Command> commands;
    List<SensorData> sensorData;

    public OpenIoTSensor() {
    }

    @JsonProperty(value = "DeviceProps")
    public DeviceProperties getDeviceProps() {
        return DeviceProps;
    }

    public void setDeviceProps(DeviceProperties DeviceProps) {
        this.DeviceProps = DeviceProps;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isIsConcentrator() {
        return isConcentrator;
    }

    public void setIsConcentrator(boolean isConcentrator) {
        this.isConcentrator = isConcentrator;
    }

    public SensorModel getModel() {
        return model;
    }

    public void setModel(SensorModel model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public List<SensorData> getSensorData() {
        return sensorData;
    }

    public void setSensorData(List<SensorData> sensorData) {
        this.sensorData = sensorData;
    }

}
