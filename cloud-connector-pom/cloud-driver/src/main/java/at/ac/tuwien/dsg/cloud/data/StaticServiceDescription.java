package at.ac.tuwien.dsg.cloud.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.exceptions.OvfReaderException;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.exceptions.UserDataException;
import at.ac.tuwien.dsg.cloud.manifest.ManifestReader;
import at.ac.tuwien.dsg.cloud.manifest.XMLManifestReader;
import at.ac.tuwien.dsg.cloud.manifest.utils.ParsingUtils;
import ch.usi.cloud.controller.common.naming.FQN;
import ch.usi.cloud.controller.common.naming.FQNException;
import ch.usi.cloud.controller.common.naming.FQNType;

public class StaticServiceDescription {

	// This is not really OK. We need either to get the list at the beginning or
	// having some other type...
	// private static ArrayList<String> eucalyptusTypes = new
	// ArrayList<String>();
	// static {
	// eucalyptusTypes.add("m1.small");
	// eucalyptusTypes.add("m1.medium");
	// eucalyptusTypes.add("m1.large");
	// eucalyptusTypes.add("m1.xlarge");
	// eucalyptusTypes.add("c1.xlarge");
	// }
	// These must be retrieved by the Platform at startup or something

	private static Logger logger = Logger
			.getLogger(StaticServiceDescription.class);

	private FQN serviceFQN;

	private boolean singleInstanceDeployment = false;

	// This array contains the roles ordered
	// using the startupOrder
	private ArrayList<VeeDescription> orderedVees;

	private String entryPoint;

	public String getEntryPoint() {
		return entryPoint;
	}

	public boolean isSingleInstanceDeployment() {
		return singleInstanceDeployment;
	}

	public void setSingleInstanceDeployment(boolean singleInstanceDeployment) {
		this.singleInstanceDeployment = singleInstanceDeployment;
	}

	public StaticServiceDescription(FQN sFqn,
			ArrayList<VeeDescription> orderedVees, String entryPoint) {
		this.serviceFQN = sFqn;
		this.orderedVees = orderedVees;
		this.entryPoint = entryPoint;
	}

	public StaticServiceDescription(FQN sFqn,
			ArrayList<VeeDescription> orderedVees) {
		this(sFqn, orderedVees, null);
	}

	public StaticServiceDescription(FQN sFqn,
			ArrayList<VeeDescription> orderedVees,
			boolean singleInstanceDeployment) {
		this.serviceFQN = sFqn;
		this.orderedVees = orderedVees;
		this.singleInstanceDeployment = singleInstanceDeployment;
	}

	// copy constructor
	public StaticServiceDescription(StaticServiceDescription stDes) {
		try {
			serviceFQN = new FQN(stDes.serviceFQN.toString(), FQNType.SERVICE);
		} catch (FQNException e) {
			e.printStackTrace();
		}

		orderedVees = new ArrayList<VeeDescription>();
		for (VeeDescription veeDes : stDes.orderedVees) {
			orderedVees.add(new VeeDescription(veeDes));
		}

		this.singleInstanceDeployment = stDes.singleInstanceDeployment;

		this.entryPoint = stDes.entryPoint;
	}

	// Build the initial configuration of the services
	public List<InstanceDescription> getInitialConfiguration() {

		List<InstanceDescription> initialConfiguration = new ArrayList<InstanceDescription>();
		for (VeeDescription vee : getOrderedVees()) {
			logger.debug(vee.getName());

			for (int i = 0; i < vee.getInitialInstances(); i++) {
				String organizationName = serviceFQN.getOrganizationName();
				String customerName = serviceFQN.getCustomerName();
				String serviceName = serviceFQN.getServiceName();
				String veeName = vee.getName();
				Integer replicaNum = i;

				FQN replicaFQN = new FQN(organizationName, customerName,
						serviceName, "", veeName, replicaNum);
				logger.debug("Adding " + replicaFQN + " to initial conf");
				try {
					initialConfiguration.add(new InstanceDescription(
							replicaFQN, "", "", "", null, null));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		logger.debug(initialConfiguration);
		return initialConfiguration;
	}

	public ArrayList<VeeDescription> getOrderedVees() {
		return orderedVees;
	}

	// public void setOrderedVees(ArrayList<VeeDescription> orderedVees) {
	// this.orderedVees = orderedVees;
	// }

	public FQN getServiceFQN() {
		return serviceFQN;
	}

	// Replicated from EucaUtils... It's a factory method better have it here.
	// Next, move to service
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

	@Override
	public boolean equals(Object arg0) {

		if (arg0 instanceof StaticServiceDescription) {

			StaticServiceDescription that = (StaticServiceDescription) arg0;

			if (! ("" + this.entryPoint).equals("" + that.entryPoint)) {
				return false;
			}

			if (this.serviceFQN != null) {
				if (!this.serviceFQN.equals(that.serviceFQN)) {
					return false;
				}
			}

			if (!this.getOrderedVees().containsAll(that.getOrderedVees())) {
				return false;
			}
			if (!that.getOrderedVees().containsAll(this.getOrderedVees())) {
				return false;
			}
			return true;
		} else {

			return super.equals(arg0);
		}
	}
}
