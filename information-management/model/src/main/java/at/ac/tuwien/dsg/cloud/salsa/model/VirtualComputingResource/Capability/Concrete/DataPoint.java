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
public class DataPoint extends Capability {

    // temperature, humidity, GPS, image, video_streaming, state
    String datatype;

    // if available based on type
    String measurementUnit;

    // reading rate
    int rate;

    /**
     * The class which implementation functions to interact with this DataPoint Some function can be: - onStateChanged() - onBufferChanged(String bufferName,
     * Object oldData, Object newData); - Stream<Object> getDataStream(String buffer); - setData(String bufferName, Object newData); - changeDataRate(DataPoint
     * datapoint, Long rate);
     */
    String managementClass;

    public DataPoint() {
        type = CapabilityType.DataPointManagement;
    }

    public DataPoint(String name, String description) {
        super(name, CapabilityType.DataPointManagement, description);
    }

    public DataPoint(String name, String description, String datatype, String measurementUnit, int rate) {
        super(name, CapabilityType.DataPointManagement, description);
        this.datatype = datatype;
        this.measurementUnit = measurementUnit;
        this.rate = rate;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getManagementClass() {
        return managementClass;
    }

    public void setManagementClass(String managementClass) {
        this.managementClass = managementClass;
    }

}
