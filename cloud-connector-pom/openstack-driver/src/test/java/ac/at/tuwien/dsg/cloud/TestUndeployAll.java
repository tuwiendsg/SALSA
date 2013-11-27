package ac.at.tuwien.dsg.cloud;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;

public class TestUndeployAll {

	public static void main(String[] args) {
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

		try {
			registry.getService("OSServiceDeployer", ServiceDeployer.class)
					.undeployAllServices("dsg", "tale", "tale", null);
		} catch (ServiceDeployerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registry.shutdown();
	}
}
