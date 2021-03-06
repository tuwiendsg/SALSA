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
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudParameter;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudParametersUser;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.localhost.LocalhostConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.openstack.OpenStackJcloud;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.openstack.OpenStackParameterStrings;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab.StratusLabConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab.StratuslabParameterStrings;

// support 
//@Component
public class MultiCloudConnector {

	//@Value("${cloudconnector.user}")
	String username;
	
	Logger logger;
	CloudInterface connector;
	CloudParametersUser paramUser;
	
	public MultiCloudConnector(Logger logger, File configFile) {
		this.logger = logger;
		paramUser = new CloudParametersUser(configFile);
	}

	public CloudInterface getCloudInplementation(SalsaCloudProviders provider) {
		CloudInterface cloud;
		try {			
			switch (provider) {
			case DSG_OPENSTACK: {
				CloudParameter param = paramUser.getParameter("dsg", "openstack");
				String keystone_endpoint = param.getParameter(OpenStackParameterStrings.KEYSTONE_ENDPOINT);
				String tenant = param.getParameter(OpenStackParameterStrings.TENANT);
				String username = param.getParameter(OpenStackParameterStrings.USERNAME);
				String password = param.getParameter(OpenStackParameterStrings.PASSWORD);
				String keyName = param.getParameter(OpenStackParameterStrings.SSH_KEY_NAME);
				String accesskey=param.getParameter(OpenStackParameterStrings.ACCESS_KEY);
				
				
				logger.info("Connection info: " + accesskey +", " + keystone_endpoint + ", " + tenant + ", " + username + ", " + password + ", " + keyName );
				
				cloud = new OpenStackJcloud(logger, keystone_endpoint, tenant, username, password, keyName);
				break;
			}
			
			case LAL_STRATUSLAB:
			{
				CloudParameter param1 = paramUser.getParameter("lal", "stratuslab");
				String user_public_key_file = param1.getParameter(StratuslabParameterStrings.user_public_key_file);
				if (user_public_key_file==null || user_public_key_file.equals("")){
					user_public_key_file = MultiCloudConnector.class.getResource("/id_rsa_hung.pub").getFile();
				}
				
				cloud = new StratusLabConnector(
						logger,
						param1.getParameter(StratuslabParameterStrings.endpoint),
						param1.getParameter(StratuslabParameterStrings.pdisk_endpoint),
						param1.getParameter(StratuslabParameterStrings.username),
						param1.getParameter(StratuslabParameterStrings.password),
						user_public_key_file,
						param1.getParameter(StratuslabParameterStrings.client_path));
				
				System.out.println(param1.getParameter(StratuslabParameterStrings.endpoint)
						+ " - " + param1.getParameter(StratuslabParameterStrings.pdisk_endpoint)
						+ " - " + param1.getParameter(StratuslabParameterStrings.username)
						+ " - " + param1.getParameter(StratuslabParameterStrings.password)
						+ " - " + user_public_key_file);
				break;
			}
			case LOCALHOST:
			{
				cloud = new LocalhostConnector(logger);
				break;
			}
			default:
				logger.error("Undefined Cloud Connector !");
				cloud = null;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cloud;
	}

	// launch an instance on a provider and return an InstanceDescription
	public InstanceDescription launchInstance(String instancename, SalsaCloudProviders provider,
			String imageId, String sshKeyName, String userData,
			String instType, int minInst, int maxInst) {
		CloudInterface cloud = getCloudInplementation(provider);
		try {
			if (cloud != null) {
				String newInstance = cloud.launchInstance(instancename, imageId,
						Arrays.asList("default"), sshKeyName, userData,
						instType, minInst, maxInst);
				InstanceDescription id = cloud
						.getInstanceDescriptionByID(newInstance);
				return id;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void removeInstance(SalsaCloudProviders provider, String instanceId) {
		CloudInterface cloud = getCloudInplementation(provider);
		try {
			cloud.removeInstance(instanceId);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
