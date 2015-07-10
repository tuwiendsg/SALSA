/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

/**
 *
 * @author hungld
 */
public class AsyncUnitCapability implements UnitCapabilityInterface {

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaEngineException {
        new Thread(new asynSpawnInstances(serviceId, nodeId, instanceId)).start();
        EngineLogger.logger.debug("This message to annonce that the error may occur after this point, because the Thread.run cannot return the ServiceInstance object, then this method just return a null.");
        return null;
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaEngineException {
        GenericUnitCapability geneCapa = new GenericUnitCapability();
        geneCapa.remove(serviceId, nodeId, instanceId);
    }
    
    
    private class asynSpawnInstances implements Runnable {
        String serviceId, topoId, nodeId;
        int instanceId;

        asynSpawnInstances(String serviceId, String nodeId, int instanceId) {
            this.serviceId = serviceId;
            this.nodeId = nodeId;
            this.instanceId = instanceId;
        }
        
        @Override
        public void run() {
            try {
                GenericUnitCapability geneCapa = new GenericUnitCapability();
                geneCapa.deploy(serviceId, nodeId, instanceId);
            } catch (SalsaEngineException e) {
                EngineLogger.logger.error(e.getMessage());
            }
        }
        
        
    }
    
}
