/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author hungld
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
