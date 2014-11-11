package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds;

import java.util.Arrays;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.jcloud.OpenStackJcloud;

public class TestRunning {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		OpenStackJcloud con = new OpenStackJcloud(null, "http://openstack.infosys.tuwien.ac.at/identity/v2.0/", "CELAR", "hung", "Coowcyurp8", "Hungld");		
		con.launchInstance("hungTestVM", "be6ae07b-7deb-4926-bfd7-b11afe228d6a", Arrays.asList("default"), "Hungld", prepareUserData(), "000000960", 1, 1);
		//con.removeInstance("6a22d9c8-1f01-4ebc-857c-5afb28812ad3");
	}

	
	
	
	public static String prepareUserData() {
		StringBuffer userDataBuffer = new StringBuffer();
		userDataBuffer.append("#!/bin/bash \n");
		userDataBuffer.append("echo \"Running the customization scripts\" \n");
		userDataBuffer.append("touch /tmp/yoyo \n");

		// install java. It ad-hoc, will be improve later
		userDataBuffer.append("apt-get -q update \n");
		userDataBuffer.append("apt-get -q -y install openjdk-7-jre \n");

		userDataBuffer.append("echo Current dir `pwd` \n");		

		return userDataBuffer.toString();
	}
	
	
}
