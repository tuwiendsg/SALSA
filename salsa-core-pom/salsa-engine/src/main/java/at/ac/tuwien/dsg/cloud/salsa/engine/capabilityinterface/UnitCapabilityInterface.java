/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;

/**
 *
 * @author hungld
 */
public interface UnitCapabilityInterface {

    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException;

    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException;
}
