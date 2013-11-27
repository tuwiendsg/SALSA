package at.ac.tuwien.dsg.cloud.salsa.service.impl;

import java.util.Arrays;
import java.util.Properties;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.JEC2ClientFactory;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.OpenStackTypica;
import at.ac.tuwien.dsg.cloud.salsa.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.stratuslab.services.impl.StratusLabConnector;

import com.xerox.amazonws.ec2.InstanceType;

// support 
public class MultiCloudConnector {
	enum CLOUD_PROVIDER {
		openstack("OPENSTACK"), stratuslab("STRATUSLAB");
		private final String providerId;

		CLOUD_PROVIDER(String providerId) {
			this.providerId = providerId;
		}
	};

	CloudInterface connector;

	// launch an instance on a provider and return an InstanceDescription
	public InstanceDescription launchInstance(String providerName,
			String imageId, String sshKeyName, String userData,
			InstanceType instType, int minInst, int maxInst) {
		try {
			CloudInterface cloud;
			switch (providerName) {
			case "OPENSTACK":
				Properties prop = new Properties();
				prop.load(MultiCloudConnector.class
						.getResourceAsStream("/cloud.properties"));
				// default value. Should get from contributed method later.
				int socketTimeout = 5000;
				int connectionTimeout = 3000;
				int connectionManagerTimeout = 6000;
				int maxRetries = 10;
				int maxConnections = 10;
				System.out.println(prop.getProperty("ch.usi.cloud.controller.eucalyptus.secretKey")+" - "+
						prop.getProperty("ch.usi.cloud.controller.eucalyptus.accessKey") +"-"+
						prop.getProperty("ch.usi.cloud.controller.eucalyptus.ccAddress"));
				
				JEC2ClientFactory cf = new JEC2ClientFactory(
						EngineLogger.logger,
						prop.getProperty("ch.usi.cloud.controller.eucalyptus.accessKey"),
						prop.getProperty("ch.usi.cloud.controller.eucalyptus.secretKey"),
						prop.getProperty("ch.usi.cloud.controller.eucalyptus.ccAddress"),
						Integer.parseInt(prop
								.getProperty("ch.usi.cloud.controller.eucalyptus.ccPort")),
						socketTimeout, connectionTimeout,
						connectionManagerTimeout, maxRetries, maxConnections);
				long retryDelayMillis = 5000;
				int deployMaxRetries = 24;
				long deployWaitMillis = 10000;
				cloud = new OpenStackTypica(EngineLogger.logger, cf,
						maxRetries, retryDelayMillis, deployMaxRetries,
						deployWaitMillis);
				break;
			case "STRATUSLAB":
				cloud = new StratusLabConnector(EngineLogger.logger);
				break;
			default:
				EngineLogger.logger.error("Undefined Cloud Provider !");
				cloud = null;
				break;
			}
			//ArrayList<String> securityGroups = new ArrayList<String>();

			// create instances
			if (cloud != null) {
				String newInstance = cloud.launchInstance(imageId,
						Arrays.asList("default"), sshKeyName, userData, instType,
						minInst, maxInst);
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

	
}
