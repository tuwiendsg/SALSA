package ac.at.tuwien.dsg.cloud;

import java.util.UUID;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;

public class TestDeployOpenStack {

	public static void main(String[] args) {

		
		
		// String manifestURL =
		// "http://www.inf.usi.ch/phd/gambi/attachments/service-manifest.xml";

		String manifestURL;
		// manifestURL =
		// "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-service-manifest.js";
		// "file:/Users/alessiogambi/Documents/TUWien/OngoingWorkNotDropBox/elasticTest/euca-interface/src/test/resources/service-manifest.js";
		// manifestURL =
		// "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-manifest.js";
		// manifestURL =
		// "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-simple-service-manifest.js";

		manifestURL = "http://www.inf.usi.ch/phd/gambi/attachments/autocles/doodle-manifest.xml";

		// Virtual machine with m1.large type
		// manifestURL =
		// "file:/Users/alessiogambi/Documents/TUWien/OngoingWorkNotDropBox/elasticTest/cloud-driver/src/test/resources/openstack-large.js";

		String[] _args = new String[5];
		_args[0] = "DEPLOY";
		_args[1] = "aaa";
		_args[2] = "bbb";
		_args[3] = "ccc";
		_args[4] = manifestURL;

		// THIS SHOULD BE ON THE CLASS PATH but for some reason the
		// getClass().resourceAsStream() fails to read it!
		System.getProperties()
				.put("at.ac.tuwien.dsg.cloud.configuration",
						"/Users/alessiogambi/Documents/TUWien/OngoingWorkNotDropBox/elasticTest/openstack-driver/src/test/resources/cloud.properties");
		// TODO Inject Autocles values if needed

		StaticServiceDescription serviceSpec = null;
		try {
			serviceSpec = StaticServiceDescriptionFactory.fromURL(manifestURL);
		} catch (ServiceDeployerException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		System.out.println("TestDeployOpenStack.main() "
				+ serviceSpec.getOrderedVees());
		UUID deployID = null;

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(CloudAppModule.class);
		builder.add(at.ac.tuwien.dsg.cloud.openstack.modules.CloudAppModule.class);

		final Registry registry = builder.build();
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

		if (deployID == null) {
			try {
				deployID = registry.getService("OSServiceDeployer",
						ServiceDeployer.class).deployService(_args[1],
						_args[2], _args[3], serviceSpec);
			} catch (ServiceDeployerException e) {
				if (e.getMessage().contains("under deployment")) {

					String _deployID = e.getMessage().substring(
							e.getMessage().lastIndexOf(" "),
							e.getMessage().length());
					System.out.println("TestDeploy.main() " + _deployID);

					deployID = UUID.fromString(_deployID);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			 try {
			 Thread.sleep(60000);
			 } catch (InterruptedException e) {
			 // TODO: handle exception
			}
		}

		try {
			registry.getService("OSServiceDeployer", ServiceDeployer.class)
					.undeployService(_args[1], _args[2], _args[3], deployID,
							serviceSpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
