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
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.DataPointTransformerInterface;

/**
 *
 * @author hungld
 */
public class AndroidSensorTransformer implements DataPointTransformerInterface<AndroidSensor>{

    @Override
    public AndroidSensor toDomainInfo(String data) {
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
        DataPoint datapoint = new DataPoint(data.getmName(), "type:" + data.getmType()+ ",version:"+data.getmVersion());
        datapoint.setMeasurementUnit("default_unit_for_android_type:"+data.getmType());
        datapoint.setRate(data.getmMinDelay());
        datapoint.setDatatype("type:"+data.getmType());
        return datapoint;
    }

  

    
    
}
