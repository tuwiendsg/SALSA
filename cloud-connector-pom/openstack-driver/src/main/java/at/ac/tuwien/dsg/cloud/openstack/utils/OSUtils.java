package at.ac.tuwien.dsg.cloud.openstack.utils;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class OSUtils {

	private static Logger logger = Logger.getLogger(OSUtils.class);
	private static ArrayList<String> eucalyptusTypes = new ArrayList<String>();

	// TODO This is bad, it would be better to use a conf file or something
	static {
		eucalyptusTypes.add("m1.small");
		eucalyptusTypes.add("m1.medium");
		eucalyptusTypes.add("m1.large");
		eucalyptusTypes.add("m1.xlarge");
		eucalyptusTypes.add("c1.xlarge");
	}

}
