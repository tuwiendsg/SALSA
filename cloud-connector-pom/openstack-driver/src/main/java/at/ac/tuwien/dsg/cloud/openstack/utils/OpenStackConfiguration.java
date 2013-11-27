package at.ac.tuwien.dsg.cloud.openstack.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * Use Tapestry Symboli like approach while refactoring this class
 * 
 * @author ubuntu
 * 
 */
public class OpenStackConfiguration {

	private static Logger logger = Logger
			.getLogger(at.ac.tuwien.dsg.cloud.openstack.utils.OpenStackConfiguration.class);

	private static String getSymbol(String sysKey, String envKey)
			throws IllegalArgumentException {

		logger.info("Getting symbol " + sysKey + " " + envKey);
		logger.info(System.getProperty(sysKey));
		logger.info(System.getenv(envKey));

		if (System.getProperties().containsKey(sysKey)) {
			return System.getProperty(sysKey);
		} else {
			if (envKey != null) {
				if (System.getenv().containsKey(envKey)) {
					return System.getenv().get(envKey);
				}
			}
		}

		StringBuffer sb = new StringBuffer();

		sb.append("The java property ");
		sb.append(sysKey);
		if (envKey != null) {
			sb.append(" and the shell environment variable ");
			sb.append(envKey);
			sb.append(" have ");
		} else {
			sb.append(" has ");
		}
		sb.append(" not been defined. Impossible to instatiate the EUCALYPTUS client !\n");

		// sb.append("System properties: ");
		// sb.append(System.getProperties());
		// sb.append("\n");
		// sb.append("Env properties: ");
		// sb.append(System.getenv());

		logger.error(sb.toString());
		throw new IllegalArgumentException(sb.toString());
	}

	// public static String get(String key) throws EucalyptusParametersException
	// {
	// if ("@eucaSecretKey".equals(key) || "@secretKey".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.EUCALYPTUS_SECRET_KEY,
	// "EC2_SECRET_KEY");
	// }
	// if ("@eucaAccessKey".equals(key) || "@accessKey".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.EUCALYPTUS_ACCESS_KEY,
	// "EC2_ACCESS_KEY");
	// }
	// if ("@novaUsername".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.NOVA_USERNAME,
	// "OS_USERNAME");
	// }
	// if ("@novaPassword".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.NOVA_PASSWORD,
	// "OS_PASSWORD");
	// }
	// if ("@novaTenantName".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.NOVA_TENANT_NAME,
	// "OS_TENANT_NAME");
	// }
	// if ("@novaTenantId".equals(key)) {
	// return getSymbol(EucalyptusPropertyNames.NOVA_TENANT_ID,
	// "OS_TENANT_ID");
	// }
	// return null;
	// }

	public static void setAccessKey(String accessKey) {
		System.setProperty(OSSymbolConstants.OS_EC2_ACCESS_KEY, accessKey);
	}

	public static String getAccessKey() throws IllegalArgumentException {

		String accessKey;

		if (System.getProperty(OSSymbolConstants.OS_EC2_ACCESS_KEY) != null) {
			accessKey = System.getProperty(OSSymbolConstants.OS_EC2_ACCESS_KEY);
		} else {
			accessKey = System.getenv("EUCA_ACCESS_KEY");

			if (accessKey == null) {
				logger.error("Neither the java property "
						+ OSSymbolConstants.OS_EC2_ACCESS_KEY
						+ " nor the shell environment variable EUCA_ACCESS_KEY have been defined, "
						+ "impossible to instatiate the EUCALYPTUS client, aborting experiment...");
				throw new IllegalArgumentException(
						"Neither the java property "
								+ OSSymbolConstants.OS_EC2_ACCESS_KEY
								+ " nor the shell environment variable EUCA_ACCESS_KEY have been defined, "
								+ "impossible to instantiate the EUCALYPTUS client, aborting experiment...");

			}
		}

		return accessKey;
	}

	public static void setSecreteKey(String secreteKey) {
		System.setProperty(OSSymbolConstants.OS_EC2_SECRET_KEY, secreteKey);
	}

	public static String getSecretKey() throws IllegalArgumentException {

		String secretKey;

		if (System.getProperty(OSSymbolConstants.OS_EC2_SECRET_KEY) != null) {
			secretKey = System.getProperty(OSSymbolConstants.OS_EC2_SECRET_KEY);
		} else {

			secretKey = System.getenv("EUCA_SECRET_KEY");

			if (secretKey == null) {
				logger.error("Neither the java property "
						+ OSSymbolConstants.OS_EC2_SECRET_KEY
						+ " nor the shell environment variable EUCA_SECRET_KEY have been defined, "
						+ "impossible to instatiate the EUCALYPTUS client, aborting experiment...");
				throw new IllegalArgumentException(
						"Neither the java property "
								+ OSSymbolConstants.OS_EC2_SECRET_KEY
								+ " nor the shell environment variable EUCA_SECRET_KEY have been defined, "
								+ "impossible to instatiate the EUCALYPTUS client, aborting experiment...");
			}
		}

		return secretKey;

	}

	public static void setCloudControllerPort(Integer port) {
		System.setProperty(OSSymbolConstants.OS_EC2_CC_PORT, port.toString());
	}

	public static Integer getCloudControllerPort()
			throws IllegalArgumentException {

		String erroMessage;

		if (System.getProperty(OSSymbolConstants.OS_EC2_CC_PORT) != null) {

			try {
				return Integer.parseInt(System
						.getProperty(OSSymbolConstants.OS_EC2_CC_PORT));
			} catch (NumberFormatException e) {
				erroMessage = "The system property "
						+ OSSymbolConstants.OS_EC2_CC_PORT
						+ " that must contain the "
						+ " value of the port of the eucalyptus cloud controller has been set to NON-Integer value";
				logger.error(erroMessage);
				throw new IllegalArgumentException(erroMessage, e);
			}

		} else {
			erroMessage = "Property " + OSSymbolConstants.OS_EC2_CC_PORT
					+ " has not been set";
			logger.error(erroMessage);
			throw new IllegalArgumentException(erroMessage);
		}

	}

	public static void setCloudControllerHost(InetAddress address) {
		System.setProperty(OSSymbolConstants.OS_EC2_CC_ADDRESS,
				address.getHostAddress());
	}
	
	

	public static InetAddress getCloudControllerHost()
			throws IllegalArgumentException {

		String erroMessage;

		if (System.getProperty(OSSymbolConstants.OS_EC2_CC_ADDRESS) != null) {

			try {
				return InetAddress.getByName(System
						.getProperty(OSSymbolConstants.OS_EC2_CC_ADDRESS));
			} catch (UnknownHostException e) {
				erroMessage = "Systme property "
						+ OSSymbolConstants.OS_EC2_CC_ADDRESS
						+ " has not been set to a valid ip-address";
				logger.error(erroMessage);
				throw new IllegalArgumentException(erroMessage, e);
			}

		} else {
			erroMessage = "Property " + OSSymbolConstants.OS_EC2_CC_ADDRESS
					+ " has not been set";
			logger.error(erroMessage);
			throw new IllegalArgumentException(erroMessage);
		}

	}

}
