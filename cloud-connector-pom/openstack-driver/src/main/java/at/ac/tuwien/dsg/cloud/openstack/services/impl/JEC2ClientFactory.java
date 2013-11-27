package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import org.slf4j.Logger;

import com.xerox.amazonws.ec2.Jec2;

public class JEC2ClientFactory {

	private Logger logger;
	private String accessKey;
	private String secretKey;
	private String cloudHost;
	private int cloudPort;

	private int socketTimeout;
	private int connectionTimeout;
	private int connectionManagerTimeout;
	private int maxRetries;
	private int maxConnections;

	public JEC2ClientFactory(Logger logger, String accessKey, String secretKey,
			String cloudHost, int cloudPort,
			//
			int socketTimeout, int connectionTimeout,
			int connectionManagerTimeout, int maxRetries, int maxConnections) {

		this.logger = logger;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.cloudHost = cloudHost;
		this.cloudPort = cloudPort;

		this.socketTimeout = socketTimeout;
		this.connectionManagerTimeout = connectionManagerTimeout;
		this.connectionTimeout = connectionTimeout;
		this.maxConnections = maxConnections;
		this.maxRetries = maxRetries;

		// System.out.println("JEC2ClientFactory.JEC2ClientFactory() Logger "
		// + logger.getName());
	}

	/**
	 * Instantiate a new Jec2 Client at each invocation. Not sure this must be
	 * declared as synchronized.
	 * 
	 * @return
	 */
	public synchronized Jec2 getNewClient() {

		Jec2 newClient = null;
		String errorMessage;

		try {
			// newClient = new Jec2(accessKey, secretKey, false, cloudHost,
			// cloudPort);

			newClient = new JEC2DecoratedClient(accessKey, secretKey, false,
					cloudHost, cloudPort, logger, maxRetries);

			newClient.setSoTimeout(socketTimeout);
			newClient.setConnectionTimeout(connectionTimeout);
			newClient.setConnectionManagerTimeout(connectionManagerTimeout);
			newClient.setMaxRetries(maxRetries);
			newClient.setMaxConnections(maxConnections);

			// This is a workaround for OpenStack/Eucalyptus. Not that we can
			// make it general with the platform symbol if we push this class
			// into the cloud-driver project
			newClient.setResourcePrefix("/services/Cloud");
			// TODO Let's try if this is important in OpenStack
			// newClient.setSignatureVersion(1);

			// logger.info("New Jec2 Client " + newClient + " - "
			// + newClient.getSignatureVersion() + " allocated in "
			// + (System.currentTimeMillis() - start));

		} catch (IllegalArgumentException e) {
			errorMessage = "Failed to instantiate a new Jec2 client";
			logger.error(errorMessage + ": \n" + e.getMessage());
			throw new RuntimeException(errorMessage, e);
		}

		return newClient;
	}
}
