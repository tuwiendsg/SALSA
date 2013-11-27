package at.ac.tuwien.dsg.cloud.openstack.utils;

public class OSSymbolConstants {
	// Actuator specific configurations
	public final static String MAX_RETRIES = "at.ac.tuwien.dsg.cloud.os.max.retries";
	public final static String RETRY_DELAY_MILLIS = "at.ac.tuwien.dsg.cloud.os.retry.delay.millis";
	public final static String DEPLOY_WAIT_MILLIS = "at.ac.tuwien.dsg.cloud.os.deploy.wait.millis";
	public final static String DEPLOY_MAX_RETRIES = "at.ac.tuwien.dsg.cloud.os.deploy.max.retries";

	// Additional informations such as pass, user names and so can be placed in
	// an external file
	public final static String CONFIGURATION_FILE = "at.ac.tuwien.dsg.cloud.os.configuration";

	// Key names for using the EC2 style API
	public static final String OS_EC2_CC_ADDRESS = "ch.usi.cloud.controller.eucalyptus.ccAddress";
	public static final String OS_EC2_CC_PORT = "ch.usi.cloud.controller.eucalyptus.ccPort";
	public static final String OS_EC2_ACCESS_KEY = "ch.usi.cloud.controller.eucalyptus.accessKey";
	public static final String OS_EC2_SECRET_KEY = "ch.usi.cloud.controller.eucalyptus.secretKey";

	// Key names for using the Nova style API
	public static final String NOVA_USERNAME = "ch.usi.cloud.nova.userName";
	public static final String NOVA_PASSWORD = "ch.usi.cloud.nova.password";
	public static final String NOVA_TENANT_NAME = "ch.usi.cloud.nova.tenant.name";
	public static final String NOVA_TENANT_ID = "ch.usi.cloud.nova.tenant.id";

	// Key names for JEC2 clients
	public static final String CLIENT_CONNECTION_TIMEOUT = "ch.usi.cloud.ec2.connection.timeout";
	public static final String CLIENT_SO_TIMEOUT = "ch.usi.cloud.ec2.socket.timeout";
	public static final String CLIENT_CONNECTION_MANAGER_TIMEOUT = "ch.usi.cloud.ec2.connectionmanager.timeout";
	public static final String CLIENT_MAX_RETRIES = "ch.usi.cloud.ec2.max.retries";
	public static final String CLIENT_MAX_CONNECTIONS = "ch.usi.cloud.ec2.max.connections";

}
