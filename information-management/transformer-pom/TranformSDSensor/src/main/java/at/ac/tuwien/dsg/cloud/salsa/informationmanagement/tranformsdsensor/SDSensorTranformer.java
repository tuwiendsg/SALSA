/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.tranformsdsensor;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.GatewayResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The rawData is the content of salsa.meta file, which defines the list of capability
 *
 * @author hungld
 */
public class SDSensorTranformer implements GatewayResourceDiscoveryInterface<SDSensorMeta> {

    @Override
    public SDSensorMeta validateAndConvertToDomainModel(String rawData, String sensorMetaFilePath) {
        Properties p = new Properties();
        StringReader reader = new StringReader(rawData);
        String baseDir=sensorMetaFilePath.substring(0,sensorMetaFilePath.lastIndexOf("/"));
        try {
            p.load(reader);
            SDSensorMeta meta = new SDSensorMeta(p.getProperty("name"), p.getProperty("type"), p.getProperty("rate"), p.getProperty("protocol"));
            for (Object keyObj : p.keySet()) {
                String key = (String) keyObj;
                if (key.startsWith("action.")) {
                    String actionName = key.substring(key.indexOf(".") + 1);
                    meta.getActions().put(actionName, baseDir+"/"+p.getProperty(key));
                }
            }
            return meta;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public DataPoint toDataPoint(SDSensorMeta data) {
        DataPoint dp = new DataPoint(data.getName(), "DataPoint_"+data.getName(), "SD Sensor", data.getType(), "N/A", Integer.parseInt(data.getRate()));
        return dp;
    }

    @Override
    public List<ControlPoint> toControlPoint(SDSensorMeta data) {
        List<ControlPoint> cps = new ArrayList<>();
        for(String key: data.getActions().keySet()){
            ControlPoint cp = new ControlPoint(data.getName(), key, "Action " + key, ControlPoint.InvokeProtocol.LOCAL_EXECUTE, data.getActions().get(key), "");
            cps.add(cp);
        }
        return cps;
    }

    @Override
    public ExecutionEnvironment toExecutionEnvironment(SDSensorMeta data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CloudConnectivity toCloudConnectivity(SDSensorMeta data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
