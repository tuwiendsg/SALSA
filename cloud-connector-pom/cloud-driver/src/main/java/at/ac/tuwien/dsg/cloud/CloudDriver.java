package at.ac.tuwien.dsg.cloud;

import java.util.UUID;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;

/**
 * This class is in charge of doing all the initializations, and exposes the
 * static methods for deploy and un-deploy
 * 
 * @author alessiogambi
 * 
 */
public class CloudDriver {

	private static CloudDriver _INSTANCE;

	private Registry registry;

	private CloudDriver() {

		try {
			RegistryBuilder builder = new RegistryBuilder();

			// IMPORTANT: This will load all the Modules in the classpath !
			IOCUtilities.addDefaultModules(builder);

			// Manually Add Modules classes... Will this conflict with default
			// modules ?
			builder.add(CloudAppModule.class);

			// TODO Autoload ?
			registry = builder.build();
			registry.performRegistryStartup();

			// // Register the shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					// for operations done from this thread
					registry.cleanupThread();
					// call this to allow services clean shutdown
					registry.shutdown();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			registry = null;
		}

	}

	public static synchronized CloudDriver getInstance() {
		if (_INSTANCE == null) {
			_INSTANCE = new CloudDriver();
		}

		return _INSTANCE;
	}

	private static String getCloudDeployerName(String cloud) {

		if (cloud == null || cloud.length() == 0) {
			return "OSServiceDeployer";
		}

		if ("OpenStack".equalsIgnoreCase(cloud)) {
			return "OSServiceDeployer";
		} else if ("Amazon".equalsIgnoreCase(cloud)) {
			return "AmazonServiceDeployer";
		} else {
			throw new IllegalArgumentException("Cloud " + cloud
					+ " is invalid !");
		}
	}

	public static UUID deployService(String cloud, String organizationName,
			String customerName, String serviceName,
			StaticServiceDescription serviceSpec)
			throws ServiceDeployerException {
		UUID deployID = null;

		try {
			deployID = getInstance().registry.getService(
					getCloudDeployerName(cloud), ServiceDeployer.class)
					.deployService(organizationName, customerName, serviceName,
							serviceSpec);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return deployID;

	}

	public static void undeployService(String cloud, String organizationName,
			String customerName, String serviceName, UUID deployID) {
		undeployService(cloud, organizationName, customerName, serviceName,
				deployID, null);
	}

	public static void undeployService(String cloud, String organizationName,
			String customerName, String serviceName, UUID deployID,
			StaticServiceDescription serviceSpec) {
		try {
			getInstance().registry.getService(getCloudDeployerName(cloud),
					ServiceDeployer.class).undeployService(organizationName,
					customerName, serviceName, deployID, serviceSpec);
		} catch (ServiceDeployerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void undeployAllServices(String cloud,
			String organizationName, String customerName, String serviceName) {
		try {
			getInstance().registry.getService(getCloudDeployerName(cloud),
					ServiceDeployer.class).undeployAllServices(
					organizationName, customerName, serviceName, null);
		} catch (ServiceDeployerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		try {
			System.out.println("Starting main ");

			for (int i = 0; i < args.length; i++) {
				System.out.println("CloudDriver.main() " + i + ") " + args[i]);
			}

			if ("DEPLOY".equalsIgnoreCase(args[0])) {
				// Create the StaticServiceObject
				StaticServiceDescription service = StaticServiceDescriptionFactory
						.fromURL(args[4]);
				getInstance().registry.getService("OSServiceDeployer",
						ServiceDeployer.class).deployService(args[1], args[2],
						args[3], service);
			} else if ("UNDEPLOY".equalsIgnoreCase(args[0])) {
				// Create the StaticServiceObject
				String _deployID = args[4];

				if ("*".equals(_deployID)) {
					getInstance().registry.getService("OSServiceDeployer",
							ServiceDeployer.class).undeployAllServices(args[1],
							args[2], args[3], null);
				} else {
					getInstance().registry.getService("OSServiceDeployer",
							ServiceDeployer.class).undeployService(args[1],
							args[2], args[3], UUID.fromString(_deployID), null);
				}
			} else {

				throw new Exception("Wrong command " + args[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
