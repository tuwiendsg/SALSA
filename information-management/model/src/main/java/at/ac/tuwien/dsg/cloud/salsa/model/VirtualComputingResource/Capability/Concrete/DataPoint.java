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
import java.util.stream.Stream;

/**
 *
 * @author hungld
 */
public abstract class DataPoint extends Capability{

    Map<String, Object> dataBuffers;
    String state;

    /**
     * When the state of the data point is changed, e.g. the device is disconnected
     */
    public abstract void onStateChanged();

    public abstract void onBufferChanged(String bufferName, Object oldData, Object newData);

    public abstract Stream<Object> getDataStream(String buffer);
    
    public abstract void setData(String bufferName, Object newData);

    public abstract void changeDataRate(DataPoint datapoint, Long rate);

    public DataPoint() {
        type = CapabilityType.DataPointManagement;
    }
    
    
    public Map<String, Object> getDataBuffers() {
        if (dataBuffers == null){
            dataBuffers = new HashMap<>();
        }
        return dataBuffers;
    }

    public void setDataBuffers(Map<String, Object> dataBuffers) {
        this.dataBuffers = dataBuffers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
