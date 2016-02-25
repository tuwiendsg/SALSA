/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;

/**
 * The transformer convert the data (in text format like JSON, XML) into the model The implementation of transformer may need to include its model by itself
 *
 * @author hungld
 * @param <ResourceDomainClass> the class to tranform from
 */
public interface GatewayResourceDiscoveryInterface<ResourceDomainClass> extends DriverInterface{

    /**
     * This is a general processes to read raw information for all transformers
     * The collector will based on the configuration and read the data before passing to the Capability contructor
     */    

    /**
     * The constructor should implement how to convert from e.g JSON or XML to the DomainClass
     * This comply with the VALIDATE step
     * @param rawData is raw information read from the Driver
     * @return a DomainClass to use later
     */
    public ResourceDomainClass validateAndConvertToDomainModel(String rawData);

    /**
     * Then how to convert the data to the Capabilities
     *This comply with the CONSTRUCTION step
     * @param data
     * @return
     */
    public DataPoint toDataPoint(ResourceDomainClass data);
    
    public ControlPoint toControlPoint(ResourceDomainClass data);
    
    public ExecutionEnvironment toExecutionEnvironment(ResourceDomainClass data);
    
    public CloudConnectivity toCloudConnectivity(ResourceDomainClass data);

}
