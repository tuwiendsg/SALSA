package at.ac.tuwien.dsg.cloud.manifest.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DeploymentPropertiesManagementUtils {

	// All the fields of this class that begins with ovf_value
	// Are used to define the value of a static property
	// In practice if we read from ovf a static property with name $PROP_NAME
	// And given the String of this property we instantiate a StaticProperty
	// object
	// By using the getValue() method of this class the client
	// will retrieve the value of the field named ovf_value_$PROP_NAME
	public static final String ovf_value_privateIp = "`ifconfig -a | grep \"inet addr\" | grep -v \"127.0.0.1\" | "
			+ "sed -e \"s/inet addr://g\" | sed -e \"s/\\ Bcast:.*//g\" | "
			+ "sed -e \"s/^[ \\t]*//;s/[ \\t]*$//g\"`";

	public static String euca_ovf_namespace_uri = "http://open.eucalyptus.com/";

	public static String euca_ovf_userdata_prefix = "USERDATA:";

	public static ArrayList<String> userDataReservedKeys = new ArrayList<String>(
			Arrays.asList("REPLICA_FQN"
			// this property is used to inject the replica FQN in each instance
			));

	public static ArrayList<String> requiredlaunchParametersKeys = new ArrayList<String>(
			Arrays.asList("INSTANCE_TYPE", "BASE_EMI"));

	public static ArrayList<String> launchParametersKeys = new ArrayList<String>(
			Arrays.asList("SSH_KEY", "SECURITY_GROUP", "INSTANCE_TYPE",
					"BASE_EMI"));

	// For more information about the fields of this Typica class see the
	// Javadoc at
	// http://typica.s3.amazonaws.com/com/xerox/amazonws/ec2/ReservationDescription.Instance.html
	public static String getDynamicPropsClass = "ch.usi.cloud.controller.euca.impl.DynamicServiceDescription";

	// This HashMap maps the name field of a DynamicProperty instance
	// to a given method of the class indicated by staticgetDynamicPropClass
	// In this way given a Property the framework automatically knows how the
	// retrieve its value
	public static HashMap<String, String> dynamicPropertyRetrievalMethods = new HashMap<String, String>();
	static {
		dynamicPropertyRetrievalMethods.put("privateIp", "getPrivateIp");
		dynamicPropertyRetrievalMethods.put("publicIp", "getPublicIp");
	}

}
