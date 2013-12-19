package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import ch.usi.cloud.controller.common.naming.FQN;

/**
 * Hello world!
 * 
 */
public class ToscaParserToStaticDescription {
	public static StaticServiceDescription parse(TDefinitions def) {		
		ArrayList<VeeDescription> veelist = new ArrayList<VeeDescription>();
		// create service FQN here
		FQN sFQN = new FQN("org", "cus", "ser");	// just temp will change soon
		// list the Vee here
		List<TNodeTemplate> toscaNodeList = ToscaStructureQuery
				.getNodeTemplateList(def);
		List<TNodeTemplate> vmNodeList = ToscaStructureQuery.getNodeTemplatesOfTypeList("OPERATING_SYSTEM", def);		
		for (TNodeTemplate node : vmNodeList) {
			ToscaVMNodeTemplatePropertiesEntend nodeProp = (ToscaVMNodeTemplatePropertiesEntend)node.getProperties().getAny();
			HashMap<String, String> properties = new HashMap<String, String>();
			// TODO: convert Tosca to hashmap properties
			VeeDescription tVee = new VeeDescription(node.getId(),
					nodeProp.getBaseImage(), properties, node.getMinInstances(),
					Integer.parseInt(node.getMaxInstances()),
					Integer.parseInt(node.getMaxInstances()),
					"@dynamicInstanceType", "@dynamicSshKeyName",
					"@dynamicSecurityGroups");
			veelist.add(tVee);
		}

		StaticServiceDescription sd = new StaticServiceDescription(sFQN,
				veelist);

		return sd;
	}
}
