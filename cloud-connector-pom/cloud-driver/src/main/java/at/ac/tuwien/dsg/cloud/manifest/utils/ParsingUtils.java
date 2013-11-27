package at.ac.tuwien.dsg.cloud.manifest.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.exceptions.OvfReaderException;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.exceptions.UserDataException;
import at.ac.tuwien.dsg.cloud.manifest.ManifestReader;
import at.ac.tuwien.dsg.cloud.manifest.XMLManifestReader;
import at.ac.tuwien.dsg.cloud.manifest.xml.Property;
import ch.usi.cloud.controller.common.naming.FQN;

public class ParsingUtils {

	private static Logger logger = Logger.getLogger(ParsingUtils.class);

	public static HashMap<String, String> getUserDataFromManifest(
			ManifestReader reader, String veeName) throws OvfReaderException,
			UserDataException {

		String errorMessage;

		HashMap<String, String> userDataProps = new HashMap<String, String>();

		List<Property> props = reader.getProductSectionForVee(veeName);

		for (Property prop : props) {

			if (prop.getKey()
					.startsWith(
							DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix)) {

				// Get the userdata key
				String userDataPropKey = prop
						.getKey()
						.substring(
								prop.getKey()
										.indexOf(
												DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix)
										+ DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix
												.length());

				logger.debug("Userdata definition has been found for VEE "
						+ veeName + "\n" + "\t Key: \t" + userDataPropKey
						+ "\n" + "\t Value: \t" + prop.getValue());

				// Check that the userdata key is not in the list of reserved
				// keys
				if (DeploymentPropertiesManagementUtils.userDataReservedKeys
						.contains(userDataPropKey)) {

					errorMessage = "The userdata key "
							+ userDataPropKey
							+ " is in the list of reserved user data keys"
							+ " you must avoid to use keys "
							+ DeploymentPropertiesManagementUtils.userDataReservedKeys
							+ " when you pass user-data"
							+ " from the manifest to the eucalyptus platform";

					logger.error(errorMessage);
					throw new UserDataException(errorMessage);

				}

				userDataProps.put(prop.getKey(), prop.getValue());
			} else if (prop.getKey().compareTo("STARTUP") == 0) {
				// Get the userdata key
				String userDataPropKey = prop.getKey();

				logger.debug("Startup definition has been found for VEE "
						+ veeName + "\n" + "\t Key: \t" + userDataPropKey
						+ "\n" + "\t Value: \t" + prop.getValue());

				userDataProps.put(prop.getKey(), prop.getValue());
			} else {
				logger.debug("Manifest Property : " + prop.getKey()
						+ " is not a valid USERDATA or STARTUP !");
			}
		}

		return userDataProps;
	}

	public static HashMap<String, String> getaunchParametersFromManifest(
			ManifestReader reader, String veeName) {

		String errorMessage;

		ArrayList<String> definedPropsKeys = new ArrayList<String>();

		HashMap<String, String> launchParameters = new HashMap<String, String>();

		List<Property> props;
		try {
			props = reader.getProductSectionForVee(veeName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		// Create a list containing all the defined keys
		for (Property prop : props) {
			if (!prop
					.getKey()
					.startsWith(
							DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix)) {
				definedPropsKeys.add(prop.getKey());
			}
		}

		logger.debug("LaunchParamters retrieved from manifest: "
				+ definedPropsKeys.toString());

		// Loop on all the required launch parameters
		for (String launchParam : DeploymentPropertiesManagementUtils.requiredlaunchParametersKeys) {

			// Check that the required LaunchParameter has been defined
			if (!definedPropsKeys.contains(launchParam)) {
				errorMessage = "The required launch parameter "
						+ launchParam
						+ " has not been defined in "
						+ "the Eucalyptus ProductSection"
						+ " of VEE "
						+ veeName
						+ ", the keys of the required launch parameters are "
						+ DeploymentPropertiesManagementUtils.requiredlaunchParametersKeys;

				logger.error(errorMessage);
				throw new IllegalArgumentException(errorMessage);

			} else {

				for (Property prop : props) {

					if (prop.getKey().equals(launchParam)) {

						logger.debug("Required launch parameter " + launchParam
								+ " has been" + "found for VEE " + veeName
								+ ": \n" + "\t Value: \t" + prop.getValue());

						launchParameters.put(prop.getKey(), prop.getValue());

					}

				}

			}

		}

		return launchParameters;

	}

	public static StaticServiceDescription getStaticServiceDescriptionFromManifest(
			FQN serviceFQN, String manifestURL) throws ServiceDeployerException {

		ArrayList<VeeDescription> instances = new ArrayList<VeeDescription>();
		String errorMessage;
		HashMap<String, String> userdataProps;
		HashMap<String, String> launchParameters;
		Integer minInstancesParam;
		Integer maxInstancesParam;
		Integer initInstancesParam;
		String instanceType = "";

		// TODO
		// In the following try catch block I'm using RunTimeException
		// because the user can't recover from this exceptional cases
		// is this the best way to handle these exception ??? Investigate
		// What is the common practice ?
		// /NOT ANYMORE now I',m using ServiceDeployerException

		URL url;
		try {
			url = new URL(manifestURL);
			URLConnection connection = url.openConnection();
			ManifestReader reader = new XMLManifestReader(
					connection.getInputStream());

			// Get array which indicates the deployment order
			// In this way by looping on this array we can add each
			// InstanceDescription to the ServiceDescription
			// in the same order the instances will be deployed (This is the
			// convention)
			List<String> startupOrderArray = reader
					.getVirtualSystemsStartupOrder();

			for (String VEEName : startupOrderArray) {

				// Get all the parameters we need to create an
				// InstanceDescription

				userdataProps = ParsingUtils.getUserDataFromManifest(reader,
						VEEName);

				launchParameters = ParsingUtils.getaunchParametersFromManifest(
						reader, VEEName);

				minInstancesParam = reader.getMinInstancesForVee(VEEName);

				maxInstancesParam = reader.getMaxInstancesForVee(VEEName);

				initInstancesParam = reader.getInitInstancesForVee(VEEName);

				// TODO : Here we may to capture KPIs and other specifications
				// for the service

				// Just get the plain string
				instanceType = launchParameters.get("INSTANCE_TYPE").toString();

				// We must check that the given instance type is supported by
				// Eucalyptus
				// if (!eucalyptusTypes.contains(instanceType)) {
				//
				// logger.warn("The instanceType "
				// + launchParameters.get("INSTANCE_TYPE")
				// + " extracted from manifest "
				// + "for VEE "
				// + VEEName
				// +
				// " is not in the list of Eucalyptus-Supported instances types: "
				// + eucalyptusTypes + ". "
				// + "The deployer will use the defult instanceType "
				// + InstanceType.DEFAULT.getTypeId());
				//
				// // default value
				// instanceType = InstanceType.DEFAULT.toString();
				// }

				VeeDescription instDes = new VeeDescription(VEEName,
						launchParameters.get("BASE_EMI"), userdataProps,
						initInstancesParam, maxInstancesParam,
						minInstancesParam, instanceType,
						launchParameters.get("SSH_KEY"),
						launchParameters.get("SECURITY_GROUP"));

				instances.add(instDes);

				instanceType = "";
			}

		} catch (MalformedURLException e) {
			errorMessage = "The manifestURL with value " + manifestURL
					+ " is malformed";

			logger.error(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		} catch (IOException e) {
			errorMessage = "An IOException has occured while trying to open connection to  "
					+ manifestURL;

			logger.error(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		} catch (OvfReaderException e) {
			errorMessage = "An OvfReaderException has occured while trying to parse the manifest at "
					+ manifestURL;

			logger.error(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		} catch (UserDataException e) {
			errorMessage = "The user-data defined in the manifest at "
					+ manifestURL + " are malformed";

			logger.error(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			errorMessage = "The manifest doesn't contain all the required "
					+ "LaunchParameters for deploying instances on the eucalyptus platform";

			logger.error(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		}

		return new StaticServiceDescription(serviceFQN, instances);

	}

}
