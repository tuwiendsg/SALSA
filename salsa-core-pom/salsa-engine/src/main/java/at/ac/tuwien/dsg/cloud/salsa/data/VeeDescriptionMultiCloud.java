package at.ac.tuwien.dsg.cloud.salsa.data;

import java.util.HashMap;

import at.ac.tuwien.dsg.cloud.data.VeeDescription;

public class VeeDescriptionMultiCloud extends VeeDescription {
	
	String cloudProvider;
	
	// reconstruction
	public VeeDescriptionMultiCloud(String name, String baseImageId,
			HashMap<String, String> properties, int initialInstances,
			int maxInstances, int minInstances, String instanceType,
			String sshKeyName, String... securityGroups){
		super(name, baseImageId, properties, initialInstances, maxInstances, minInstances, instanceType, sshKeyName, securityGroups);
	}

	public String getCloudProvider() {
		return cloudProvider;
	}

	public void setCloudProvider(String cloudProvider) {
		this.cloudProvider = cloudProvider;
	}
		
}
