/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import generated.oasis.tosca.TDefinitions;

/**
 *
 * @author hungld
 */
public interface WholeAppCapabilityInterface {

    public CloudService addService(String serviceName, TDefinitions def) throws SalsaException;

    public boolean cleanService(String serviceId) throws SalsaException;
}
