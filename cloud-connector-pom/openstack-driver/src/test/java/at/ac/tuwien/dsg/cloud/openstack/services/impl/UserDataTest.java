package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import java.util.UUID;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription;
import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.InstanceType;

public class UserDataTest {

	public static void main(String[] args) {
		try {

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

			OSController controller = new OSController(
					LoggerFactory.getLogger(OSController.class),
					registry.getService(SymbolSource.class),
					registry.getService("OpenStackTypica", CloudInterface.class),
					registry.getService(UserDataService.class), registry
							.getService(InstanceService.class)

			);

			String manifestURL = "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-clients-manifest.js";
			String organizationName = "test";
			String customerName = "test";
			String serviceName = "test";
			FQN serviceFQN = new FQN(organizationName, customerName,
					serviceName);

			StaticServiceDescription serviceSpec = new StaticServiceDescription(
					serviceFQN, StaticServiceDescriptionFactory.fromURL(
							manifestURL).getOrderedVees());
			DynamicServiceDescription service = new DynamicServiceDescription(
					serviceSpec);

			UUID deployID = UUID.randomUUID();
			String veeName = "clients";

			FQN replicaFQN = new FQN(organizationName, customerName,
					serviceName, "", veeName, 0);
			VeeDescription vee = service.getVeeDescription(veeName);
			InstanceType type = InstanceType.DEFAULT;

			String userdata = controller.prepareUserData(service, vee, 0,
					replicaFQN, type, deployID);

			System.out.println("UserDataTest.main() " + userdata);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
