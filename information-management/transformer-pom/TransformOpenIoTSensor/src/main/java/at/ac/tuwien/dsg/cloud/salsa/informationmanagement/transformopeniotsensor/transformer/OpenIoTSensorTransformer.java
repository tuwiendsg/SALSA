/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor.transformer;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.DataPointTransformerInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor.OpenIoTSensor;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor.OpenIoTSensorWrapper;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author hungld
 */
public class OpenIoTSensorTransformer implements DataPointTransformerInterface<OpenIoTSensorWrapper> {

    @Override
    public OpenIoTSensorWrapper toDomainInfo(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, OpenIoTSensorWrapper.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public DataPoint toDataPoint(OpenIoTSensorWrapper wrapper) {
        if (wrapper==null){
            System.out.println("Something happen, data is null");
            return null;
        }
        OpenIoTSensor data = wrapper.getData();
        if (data==null){
            System.out.println("Something happen, data is null");
        }
        DataPoint datapoint = new DataPoint(data.getAsset().getName(), data.getAsset().getDescription());
        datapoint.setDatatype(data.getModel().toString());
//        if (data.getSensorData() != null && data.getSensorData().getMs() != null) {
//            datapoint.setMeasurementUnit(data.getSensorData().getMs().getU());
//        }
        return datapoint;
    }

}
