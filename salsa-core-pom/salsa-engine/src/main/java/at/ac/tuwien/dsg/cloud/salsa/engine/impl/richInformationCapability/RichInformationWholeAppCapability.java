/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *               
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.UnitInstanceDAO;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.WholeAppEnrichedTosca;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.WholeAppEnrichedTosca;
import generated.oasis.tosca.TDefinitions;
import java.util.Collections;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 * This capability is enhance with interact with ELISE service to store information and assign UUID
 * @author Duc-Hung LE
 */
public class RichInformationWholeAppCapability implements WholeAppCapabilityInterface {
    WholeAppCapabilityInterface lowerCapa = new WholeAppEnrichedTosca();
    

    @Override
    public CloudService addService(String serviceName, TDefinitions def) throws SalsaException {
        CloudService service =  lowerCapa.addService(serviceName, def);
        // save structure to elise service
        
        return service;
    }

    @Override
    public boolean cleanService(String serviceId) throws SalsaException {
        return lowerCapa.cleanService(serviceId);
    }
    
}
