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
package at.ac.tuwien.dsg.cloud.salsa.salsa_common;

import generated.oasis.tosca.TCapabilityDefinition;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityType;
import generated.oasis.tosca.TImplementationArtifact;
import generated.oasis.tosca.TNodeType;
import generated.oasis.tosca.TNodeTypeImplementation;
import generated.oasis.tosca.TOperation;
import generated.oasis.tosca.TRequirementDefinition;

/**
 *
 * @author Duc-Hung Le
 */
public class GenerateService {
    public static void main(String[] args) {
        
        TDefinitions def = new TDefinitions();
        
        def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        
        TNodeType type = new TNodeType();
        type.setCapabilityDefinitions(null);
        TNodeType.CapabilityDefinitions cap = new TNodeType.CapabilityDefinitions();
        TCapabilityDefinition capdef = new TCapabilityDefinition();
        capdef.setName("test");
        TNodeType.Interfaces inter = new TNodeType.Interfaces();
        inter.getInterface().get(0).setName(null);
        TOperation oper = new TOperation();
        oper.setInputParameters(null);
        TEntityType.PropertiesDefinition p = new TEntityType.PropertiesDefinition();        
        
        TNodeTypeImplementation typeImp = new TNodeTypeImplementation();
        typeImp.setImplementationArtifacts(null);
        TImplementationArtifact art = new TImplementationArtifact();
        art.setInterfaceName(null);
        
        type.setCapabilityDefinitions(cap);
        TNodeType.CapabilityDefinitions de = new TNodeType.CapabilityDefinitions();
        de.getCapabilityDefinition();
        TCapabilityDefinition tdef = new TCapabilityDefinition();
        
        
        
                
        
        
    }
}
