package at.ac.tuwien.dsg.cloud.manifest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;

public class StaticServiceDescriptionFactory {

	private static Logger logger = Logger
			.getLogger(StaticServiceDescriptionFactory.class);

	public static StaticServiceDescription fromJSONStream(
			InputStream jsonManifestFileStream) throws ServiceDeployerException {

		return parseManifest(new JSONManifestReader(jsonManifestFileStream));
	}

	public static StaticServiceDescription fromXMLStream(
			InputStream xmlManifestFileStream) throws ServiceDeployerException {
		return parseManifest(new XMLManifestReader(xmlManifestFileStream));
	}

	public static StaticServiceDescription fromJSON(String jsonManifestFile)
			throws ServiceDeployerException {
		return fromJSONStream(new ByteArrayInputStream(
				jsonManifestFile.getBytes()));
	}

	public static StaticServiceDescription fromXML(String xmlManifestFile)
			throws ServiceDeployerException {	
		return fromXMLStream(new ByteArrayInputStream(
				getFileBytes(new File(xmlManifestFile))));	
	}
	
	public static byte[] getFileBytes(File file) {
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    try {
	        byte[] buffer = new byte[4096];
	        ous = new ByteArrayOutputStream();
	        ios = new FileInputStream(file);
	        int read = 0;
	        while ((read = ios.read(buffer)) != -1)
	            ous.write(buffer, 0, read);
	    } catch (IOException e){
	    	System.out.println(e);
	}finally {
	        try {
	            if (ous != null)
	                ous.close();
	        } catch (IOException e) {
	        }
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	            // swallow, since not that important
	        }
	    }
	    return ous.toByteArray();
	}

	public static StaticServiceDescription fromURL(String manifestURL)
			throws ServiceDeployerException {

		String errorMessage = null;

		if (!manifestURL.endsWith("xml") && !manifestURL.endsWith("js")) {
			throw new IllegalArgumentException(
					"Manifest URL must point either to an XML or JSON file !");
		}

		try {
			URL url = new URL(manifestURL);
			URLConnection connection = url.openConnection();
			if (manifestURL.endsWith("xml")) {
				return fromXMLStream(connection.getInputStream());
			}

			else if (manifestURL.endsWith("js")) {
				return fromJSONStream(connection.getInputStream());
			} else {
				throw new IllegalArgumentException(
						"Manifest URL must point either to an XML or JSON file !");
			}

		} catch (MalformedURLException e) {
			errorMessage = "The manifestURL with value " + manifestURL
					+ " is malformed";

			logger.error(errorMessage);
			// throw new ServiceDeployerException(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		} catch (IOException e) {
			errorMessage = "An IOException has occured while trying to open connection to  "
					+ manifestURL;

			logger.error(errorMessage);
			// throw new ServiceDeployerException(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}

	private static StaticServiceDescription parseManifest(ManifestReader reader)
			throws ServiceDeployerException {

		String errorMessage;
		ArrayList<VeeDescription> instances = new ArrayList<VeeDescription>();

		// Get array which indicates the deployment order
		// In this way by looping on this array we can add each
		// InstanceDescription to the ServiceDescription
		// in the same order the instances will be deployed (This is the
		// convention)
		List<String> startupOrderArray = reader.getVirtualSystemsStartupOrder();
		String entryPoint = null;

		for (String VEEName : startupOrderArray) {

			Map<String, String> userdataProps = new HashMap<String, String>();
			Map<String, Object> launchParameters = new HashMap<String, Object>();
			Integer minInstancesParam = null;
			Integer maxInstancesParam = null;
			Integer initInstancesParam = null;
			VeeDescription instDes = null;

			try {
				entryPoint = reader.getEntryPoint();
			} catch (Throwable e) {
				// Silently ignore this one
				logger.warn("No entrypoint found !", e);
			}

			// Get all the parameters we need to create an
			try {
				userdataProps = reader.getUserDataFromManifest(VEEName);
			} catch (NullPointerException e) {
				// Silently ignore this one
				logger.warn("No userdata found !");
			} catch (Throwable e) {
				// Optional just log a warning
				logger.warn("Cannot read userdata. Cause", e.getCause());
			}

			try {
				minInstancesParam = reader.getMinInstancesForVee(VEEName);
			} catch (Throwable e) {
				errorMessage = "Error while reading min instance param !";
				logger.error(errorMessage);
				throw new ServiceDeployerException(errorMessage, e);
			}

			try {
				maxInstancesParam = reader.getMaxInstancesForVee(VEEName);
			} catch (Throwable e) {
				errorMessage = "Error while reading max instance param !";
				logger.error(errorMessage);
				throw new ServiceDeployerException(errorMessage, e);
			}

			try {
				initInstancesParam = reader.getInitInstancesForVee(VEEName);
			} catch (Throwable e) {
				errorMessage = "Error while reading initial instance param !";
				logger.error(errorMessage);
				throw new ServiceDeployerException(errorMessage, e);
			}

			try {
				launchParameters = reader.getLaunchParameters(VEEName);
			} catch (Throwable e) {
				errorMessage = "Error while reading launch params !";
				logger.error(errorMessage);
				throw new ServiceDeployerException(errorMessage, e);
			}

			// Consistency checks !
			instDes = new VeeDescription(VEEName,
					(String) launchParameters.get("BASE_EMI"),
					(HashMap<String, String>) userdataProps,
					initInstancesParam, maxInstancesParam, minInstancesParam,
					launchParameters.get("INSTANCE_TYPE").toString(),

					(String) launchParameters.get("SSH_KEY"),
					(String[]) launchParameters.get("SECURITY_GROUP")

			);

			instances.add(instDes);
		}

		return new StaticServiceDescription(null, instances, entryPoint);
	}
}
