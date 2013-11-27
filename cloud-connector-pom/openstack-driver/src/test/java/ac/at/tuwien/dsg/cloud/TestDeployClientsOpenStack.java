package ac.at.tuwien.dsg.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;
import at.ac.tuwien.dsg.cloud.modules.CloudAppModule;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;

public class TestDeployClientsOpenStack {

	public static void main(String[] args) throws IOException {
		String manifestURL = "http://www.inf.usi.ch/phd/gambi/attachments/autocles/clients-manifest.js";
		String[] _args = new String[5];

		_args[0] = "DEPLOY";
		_args[1] = "aaa";
		_args[2] = "bbb";
		_args[3] = "ccc";

		String _deployID = "321da878-4f8b-4963-8f55-daf854110465";
		// String _deployID = UUID.randomUUID().toString();

		StringBuilder stringBuilder = new StringBuilder();
		UUID deployID = UUID.fromString(_deployID);
		try {
			URL url = new URL(manifestURL);
			URLConnection connection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out
				.println("TestDeployClientsOpenStack.main() Clients manifest READ");

		String jsonManifestFile = stringBuilder.toString();
		// jsonManifestFile =
		// "{\"roles-starup\":[\"clients\"],\"clients\":{\"role-id\":\"clients\",\"role-description\":\"JMeterClients\",\"role-version\":\"1.0\",\"min-instance-count\":1,\"max-instance-count\":1,\"initial-instance-count\":1,\"image-id\":\"ami-00000212\",\"instance-type\":\"m1.small\",\"security-groups\":[\"default\"],\"ssh-key-name\":\"jopera-clients\",\"start-up\":[\"controlinterface\",\"jmeter\"],\"userdata\":{\"jmeter\":{\"env\":{\"jmx\":{\"url\":\"@jmxURL\"},\"trace\":{\"url\":\"@traceURL\"}},\"startup\":{\"J\":{\"loadbalancer\":{\"ip\":\"@@UUID@entrypoint-ip\",\"port\":8080},\"clients\":{\"number\":20},\"initial\":{\"delay\":60000,\"unit\":\"millis\"},\"experiment\":{\"id\":\"@experimentId\"},\"jopera\":{\"ip\":\"@joperaIp\",\"port\":\"@joperaPort\"}}},\"notify\":{\"url\":\"http://@joperaIp:@joperaPort/experiment/@experimentId/results\"},\"publish\":{\"to\":{\"ip\":\"@memcachedIp\",\"port\":\"@memcachedPort\"},\"objectKey\":\"@experimentId-clientResults\"}}}}}";

		String jmxURL = "http://www.inf.usi.ch/phd/gambi/attachments/autocles/doodle-clients.jmx";
		String traceURL = "http://www.inf.usi.ch/phd/gambi/attachments/autocles/doodle-trace.csv";

		jsonManifestFile = jsonManifestFile.replaceAll("@@UUID", "@@"
				+ deployID.toString());
		jsonManifestFile = jsonManifestFile.replaceAll("@jmxURL", jmxURL);
		jsonManifestFile = jsonManifestFile.replaceAll("@traceURL", traceURL);

		StaticServiceDescription serviceSpec = null;
		try {
			serviceSpec = StaticServiceDescriptionFactory
					.fromJSON(jsonManifestFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		System.getProperties()
				.put("at.ac.tuwien.dsg.cloud.configuration",
						"/Users/alessiogambi/Documents/TUWien/OngoingWorkNotDropBox/elasticTest/openstack-driver/src/main/resources/cloud.properties");

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

		UUID clientsDeployID = null;
		try {
			clientsDeployID = registry.getService("OSServiceDeployer",
					ServiceDeployer.class).deployService(_args[1], _args[2],
					_args[3], serviceSpec);
		} catch (ServiceDeployerException e) {
			if (e.getMessage().contains("under deployment")) {

				String _clientsDeployID = e.getMessage().substring(
						e.getMessage().lastIndexOf(" "),
						e.getMessage().length());
				System.out.println("TestDeploy.main() " + _clientsDeployID);

				clientsDeployID = UUID.fromString(_clientsDeployID);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
