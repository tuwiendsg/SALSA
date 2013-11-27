package at.ac.tuwien.dsg.cloud.manifest;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;

public class XMLManifestReaderTest {

	public static void main(String[] args) {

		String manifestURL;
		// manifestURL =
		// "file:src/test/resources/openstack-service-manifest.xml";
		manifestURL = "http://www.inf.usi.ch/phd/gambi/attachments/autocles/doodle-manifest.xml";

		try {

			StaticServiceDescription serviceSpec = StaticServiceDescriptionFactory
					.fromURL(manifestURL);

			for (VeeDescription vee : serviceSpec.getOrderedVees()) {
				System.out.println("XMLManifestReaderTest.main() "
						+ vee.getName() + " -- " + vee.getSecurityGroups());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
