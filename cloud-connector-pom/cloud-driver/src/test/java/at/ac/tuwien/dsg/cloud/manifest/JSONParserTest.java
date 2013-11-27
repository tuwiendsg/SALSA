package at.ac.tuwien.dsg.cloud.manifest;

import java.net.URL;
import java.net.URLConnection;

public class JSONParserTest {

	public static void main(String[] args) {

		String manifestURL;
		manifestURL = "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-manifest.js";
		manifestURL = "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-service-manifest.js";
		// manifestURL =
		// "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-service-manifest.xml";

		try {
			URL url = new URL(manifestURL);
			URLConnection connection = url.openConnection();

			ManifestReader reader = null;
			if (manifestURL.endsWith(".xml")) {
				reader = new XMLManifestReader(connection.getInputStream());
			} else {
				reader = new JSONManifestReader(connection.getInputStream());
			}
			System.out.println("The list of String "
					+ reader.getVirtualSystemsStartupOrder());

			// System.out.println("The User data of String ");
			// System.out.println(reader.getUserDataFromManifest("frontend"));
			//
			System.out.println("The Startup order ");
			System.out.println(reader.getVirtualSystemsStartupOrder());
			System.out.println("The EntryPoint (Static) ");
			System.out.println(reader.getEntryPoint());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
