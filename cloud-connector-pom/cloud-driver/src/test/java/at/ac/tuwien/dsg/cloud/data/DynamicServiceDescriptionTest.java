package at.ac.tuwien.dsg.cloud.data;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;

public class DynamicServiceDescriptionTest {

	private final File manifest = new File(
			"src/test/resources/openstack-service-manifest.xml");
	private String manifestFileURL;

	@Before
	public void setup() {
		manifestFileURL = "file:" + manifest.getAbsolutePath();
	}

	@Test
	public void checkEqualsStatic() {

		StaticServiceDescription serviceSpec1 = null;
		StaticServiceDescription serviceSpec2 = null;
		try {
			serviceSpec1 = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);

			serviceSpec2 = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		Assert.assertTrue(serviceSpec1.equals(serviceSpec2));
		Assert.assertTrue(serviceSpec2.equals(serviceSpec1));

	}

	@Test
	public void checkEqualsDynamic1() {
		StaticServiceDescription serviceSpec = null;
		try {
			serviceSpec = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		DynamicServiceDescription d1 = new DynamicServiceDescription(
				serviceSpec);
		DynamicServiceDescription d2 = new DynamicServiceDescription(
				serviceSpec);

		Assert.assertTrue(d1.equals(d2));
		Assert.assertTrue(d2.equals(d1));
	}

	@Test
	public void checkEqualsDynamic2() {

		StaticServiceDescription serviceSpec1 = null;
		StaticServiceDescription serviceSpec2 = null;
		try {
			serviceSpec1 = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);

			serviceSpec2 = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		DynamicServiceDescription d1 = new DynamicServiceDescription(
				serviceSpec1);
		DynamicServiceDescription d2 = new DynamicServiceDescription(
				serviceSpec2);

		Assert.assertTrue(d1.equals(d2));
		Assert.assertTrue(d2.equals(d1));
	}

	@Test
	public void checkEqualsDynamic3() {
		StaticServiceDescription serviceSpec1 = null;
		try {
			serviceSpec1 = StaticServiceDescriptionFactory
					.fromURL(manifestFileURL);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		DynamicServiceDescription d1 = new DynamicServiceDescription(
				serviceSpec1);
		DynamicServiceDescription d2 = new DynamicServiceDescription(d1);

		Assert.assertTrue(d1.equals(d2));
		Assert.assertTrue(d2.equals(d1));
	}

	// public static void main(String[] args) {
	// String manifestFileURL =
	// "file:/Users/alessiogambi/jopera-dev/cloud-driver/src/test/resources/openstack-clients-manifest.js";
	//
	// StaticServiceDescription serviceSpec = null;
	// try {
	// serviceSpec = StaticServiceDescriptionFactory
	// .fromURL(manifestFileURL);
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.exit(-1);
	// }
	//
	// DynamicServiceDescription d1 = new DynamicServiceDescription(
	// serviceSpec);
	//
	// System.out.println("DynamicServiceDescriptionTest.main() " + d1);
	//
	// DynamicServiceDescription d2 = new DynamicServiceDescription(d1);
	//
	// System.out.println("DynamicServiceDescriptionTest.main() " + d2);
	//
	// FQN replicaFQN = null;
	// try {
	// d2.addVeeInstance(d2.getVeeDescription("clients"),
	// new InstanceDescription(replicaFQN, null, "", "", "", ""));
	// } catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// System.out.println("DynamicServiceDescriptionTest.main() " + d1);
	//
	// System.out.println("DynamicServiceDescriptionTest.main() " + d2);
	// }
}
