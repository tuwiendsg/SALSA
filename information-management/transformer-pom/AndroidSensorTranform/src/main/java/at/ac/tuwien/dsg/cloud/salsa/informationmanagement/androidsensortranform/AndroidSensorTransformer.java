/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.androidsensortranform;

import android.hardware.AndroidSensor;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.GatewayResourceDiscoveryInterface;
import java.util.List;

/**
 * The transformer (should be renamed to DataPoint constructor later) 
 * that get data from the domain model and build the DataPoint
 * @author hungld
 */
public class AndroidSensorTransformer implements GatewayResourceDiscoveryInterface<AndroidSensor>{

    @Override
    public AndroidSensor validateAndConvertToDomainModel(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, AndroidSensor.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public DataPoint toDataPoint(AndroidSensor data) {        
        DataPoint datapoint = new DataPoint(data.getmName(), data.getmName(), "type:" + data.getmType()+ ",version:"+data.getmVersion());
        datapoint.setMeasurementUnit("default_unit_for_android_type:"+data.getmType());
        datapoint.setRate(data.getmMinDelay());
        datapoint.setDatatype("type:"+data.getmType());
        return datapoint;
    }

    // return null that means the resource have no such capability
    @Override
    public List<ControlPoint> toControlPoint(AndroidSensor data) {
        return null;
    }

    @Override
    public ExecutionEnvironment toExecutionEnvironment(AndroidSensor data) {
        return null;
    }

    @Override
    public CloudConnectivity toCloudConnectivity(AndroidSensor data) {
        return null;
    }

  

    
    
}
