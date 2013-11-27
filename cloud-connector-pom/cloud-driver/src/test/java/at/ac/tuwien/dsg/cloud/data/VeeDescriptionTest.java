package at.ac.tuwien.dsg.cloud.data;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.cloud.manifest.StaticServiceDescriptionFactory;

public class VeeDescriptionTest {

	private final File manifest = new File(
			"src/test/resources/openstack-service-manifest.xml");
	private String manifestFileURL;

	@Before
	public void setup() {
		manifestFileURL = "file:" + manifest.getAbsolutePath();
	}

	@Test
	public void checkEquals() {

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

		for (VeeDescription vee1 : serviceSpec1.getOrderedVees()) {

			for (VeeDescription vee2 : serviceSpec2.getOrderedVees()) {
				if (vee2.getName().equals(vee1.getName())) {
					Assert.assertTrue(vee1.equals(vee2));
					Assert.assertTrue(vee2.equals(vee1));
				}
			}
		}
	}

	@Test
	public void containsAll() {

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
		Assert.assertTrue(serviceSpec1.getOrderedVees().containsAll(
				serviceSpec2.getOrderedVees()));
		Assert.assertTrue(serviceSpec2.getOrderedVees().containsAll(
				serviceSpec1.getOrderedVees()));
	}

	@Test
	public void areEquals() {

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
		Assert.assertTrue(serviceSpec1.getOrderedVees().equals(
				serviceSpec2.getOrderedVees()));
		Assert.assertTrue(serviceSpec2.getOrderedVees().equals(
				serviceSpec1.getOrderedVees()));
	}

	// // FIXME This wont work because HashSet do not relies on equals of
	// // containing entries !!!
	// @Test
	// public void setEquals() {
	//
	// StaticServiceDescription serviceSpec1 = null;
	// StaticServiceDescription serviceSpec2 = null;
	// try {
	// serviceSpec1 = StaticServiceDescriptionFactory
	// .fromURL(manifestFileURL);
	//
	// serviceSpec2 = StaticServiceDescriptionFactory
	// .fromURL(manifestFileURL);
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.exit(-1);
	// }
	//
	// /*
	// * Methods of it's containing objects to determine equality.
	// *
	// * A HashSet carries an internal HashMap with <Integer(HashCode),
	// * Object> Entries and uses the equals method of the HashCode to
	// * determine equality.
	// *
	// * One way to solve the issue is to override hashCode() in the Class
	// * that you put in the Set, so that it represents your equals() criteria
	// */
	//
	// Set<VeeDescription> set1 = new HashSet<VeeDescription>();
	// set1.addAll(serviceSpec1.getOrderedVees());
	// Set<VeeDescription> set2 = new HashSet<VeeDescription>();
	// set2.addAll(serviceSpec2.getOrderedVees());
	//
	// Assert.assertTrue(set1.equals(set1));
	// Assert.assertTrue(set1.equals(set2));
	// Assert.assertTrue(set2.equals(set1));
	// Assert.assertTrue(set2.equals(set2));
	// }
}
