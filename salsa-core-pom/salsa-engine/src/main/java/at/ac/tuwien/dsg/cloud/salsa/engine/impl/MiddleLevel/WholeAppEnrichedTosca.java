/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.WholeAppCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA.ToscaEnricherSALSA;
import generated.oasis.tosca.TDefinitions;

/**
 *
 * @author hungld
 */
public class WholeAppEnrichedTosca implements WholeAppCapabilityInterface{

    @Override
    public CloudService addService(String serviceName, TDefinitions def) throws SalsaEngineException {
        // ENRICH
        ToscaEnricherSALSA enricher = new ToscaEnricherSALSA(def);
        enricher.enrichHighLevelTosca();
        WholeAppCapabilityInterface wholeApp = new WholeAppCapability();
        return wholeApp.addService(serviceName, def);
    }

    @Override
    public boolean cleanService(String serviceId) throws SalsaEngineException {
        WholeAppCapabilityInterface wholeApp = new WholeAppCapability();
        return wholeApp.cleanService(serviceId);
    }
    
}
