package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudParameter;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudParametersUser;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.JEC2ClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.OpenStackParameterStrings;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.OpenStackTypica;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.stratuslab.StratusLabConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.stratuslab.StratuslabParameterStrings;

import com.xerox.amazonws.ec2.InstanceType;

// support 
public class MultiCloudConnector {

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
			case DSG_OPENSTACK:
				CloudParameter param = paramUser.getParameter("dsg", "openstack");

				// default value. Should get from contributed method later.
				int socketTimeout = 5000;
				int connectionTimeout = 3000;
				int connectionManagerTimeout = 6000;
				int maxRetries = 10;
				int maxConnections = 10;
				System.out
						.println(param.getParameter(OpenStackParameterStrings.SECRET_KEY)
								+ " - "
								+ param.getParameter(OpenStackParameterStrings.ACCESS_KEY)
								+ "-"
								+ param.getParameter(OpenStackParameterStrings.END_POINT));

				JEC2ClientFactory cf = new JEC2ClientFactory(
						logger,
						param.getParameter(OpenStackParameterStrings.ACCESS_KEY),
						param.getParameter(OpenStackParameterStrings.SECRET_KEY),
						param.getParameter(OpenStackParameterStrings.END_POINT),
						Integer.parseInt(param
								.getParameter(OpenStackParameterStrings.PORT)),
						socketTimeout, connectionTimeout,
						connectionManagerTimeout, maxRetries, maxConnections);
				long retryDelayMillis = 5000;
				int deployMaxRetries = 24;
				long deployWaitMillis = 10000;
				String sshName = param.getParameter(OpenStackParameterStrings.SSH_KEY_NAME);
				cloud = new OpenStackTypica(logger, cf, maxRetries,
						retryDelayMillis, deployMaxRetries, deployWaitMillis, sshName);
				break;
			case LAL_STRATUSLAB:
				CloudParameter param1 = paramUser.getParameter("lal",
						"stratuslab");
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
	public InstanceDescription launchInstance(SalsaCloudProviders provider,
			String imageId, String sshKeyName, String userData,
			InstanceType instType, int minInst, int maxInst) {
		CloudInterface cloud = getCloudInplementation(provider);
		try {
			if (cloud != null) {
				String newInstance = cloud.launchInstance(imageId,
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
