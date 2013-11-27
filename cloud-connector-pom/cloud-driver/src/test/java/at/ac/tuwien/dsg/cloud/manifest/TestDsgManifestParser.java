package at.ac.tuwien.dsg.cloud.manifest;

import java.net.URL;
import java.net.URLConnection;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

public class TestDsgManifestParser // extends IOCTestCase
{

	public static void main(String[] args) {

		try {
			RegistryBuilder builder = new RegistryBuilder();

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

			// This should work
			String manifestURL = "http://www.inf.usi.ch/phd/gambi/attachments/service-manifest.xml";
			URL url = new URL(manifestURL);
			URLConnection connection = url.openConnection();
			// OvfReader reader = new OvfReader(connection.getInputStream());
			// DsgOvfManifestReader2 reader = new DsgOvfManifestReader2(
			// connection.getInputStream(),
			// LoggerFactory.getLogger(TestDsgManifestParser.class));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
