/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;

/**
 * The transformer convert the data (in text format like JSON, XML) into the model The implementation of transformer may need to include its model by itself
 *
 * @author hungld
 * @param <SensorDomainClass> the class to tranform from
 */
public interface DataPointTransformerInterface<SensorDomainClass> {

    /**
     * This is a general processes to read raw information for all transformers
     * The collector will based on the configuration and read the data before passing to the transformer
     */
    
    
    

    /**
     * The transformer should implement how to convert from e.g JSON or XML to the DomainClass
     *
     * @param data is raw information read from the Reader
     * @return a DomainClass to use later
     */
    public abstract SensorDomainClass toDomainInfo(String data);

    /**
     * Then how to convert the data to the DataPoint
     *
     * @param data
     * @return
     */
    public abstract DataPoint toDataPoint(SensorDomainClass data);

}
