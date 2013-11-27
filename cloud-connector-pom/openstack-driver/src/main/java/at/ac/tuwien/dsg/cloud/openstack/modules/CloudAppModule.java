package at.ac.tuwien.dsg.cloud.openstack.modules;

import java.net.UnknownHostException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.openstack.services.impl.JEC2ClientFactory;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.OSController;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.OSServiceDeployer;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.OpenStackTypica;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.PlatformUserDataServiceFilter;
import at.ac.tuwien.dsg.cloud.openstack.services.impl.RemoteValuesUserDataServiceFilter;
import at.ac.tuwien.dsg.cloud.openstack.utils.OSSymbolConstants;
import at.ac.tuwien.dsg.cloud.services.CloudController;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.DelayInjectionService;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.ResourceMonitor;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import at.ac.tuwien.dsg.cloud.services.UserDataServiceFilter;
import at.ac.tuwien.dsg.cloud.services.impl.PostDelayedCloudInterface;
import at.ac.tuwien.dsg.cloud.services.impl.PreDelayedCloudInterface;
import at.ac.tuwien.dsg.cloud.services.impl.ResourceMonitoringCloudInterface;

public class CloudAppModule {

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(OSSymbolConstants.MAX_RETRIES, "5");
		configuration.add(OSSymbolConstants.RETRY_DELAY_MILLIS, "5000");
		configuration.add(OSSymbolConstants.DEPLOY_WAIT_MILLIS, "10000");
		configuration.add(OSSymbolConstants.DEPLOY_MAX_RETRIES, "24");

		configuration.add(OSSymbolConstants.CLIENT_CONNECTION_TIMEOUT, "3000");
		configuration.add(OSSymbolConstants.CLIENT_SO_TIMEOUT, "5000");
		configuration.add(OSSymbolConstants.CLIENT_CONNECTION_MANAGER_TIMEOUT,
				"6000");
		configuration.add(OSSymbolConstants.CLIENT_MAX_RETRIES, "10");
		configuration.add(OSSymbolConstants.CLIENT_MAX_CONNECTIONS, "10");

	}

	@ServiceId("PlainOpenStackTypica")
	public CloudInterface buildPlainOpenStackTypica(
			Logger logger,
			JEC2ClientFactory jec2ClientFactory,
			@Symbol(OSSymbolConstants.MAX_RETRIES) Integer maxRetries,
			@Symbol(OSSymbolConstants.RETRY_DELAY_MILLIS) Long retryDelayMillis,
			@Symbol(OSSymbolConstants.DEPLOY_MAX_RETRIES) Integer deployMaxRetries,
			@Symbol(OSSymbolConstants.DEPLOY_WAIT_MILLIS) Long deployWaitMillis)
			throws UnknownHostException {

		return new OpenStackTypica(logger, jec2ClientFactory, maxRetries,
				retryDelayMillis, deployMaxRetries, deployWaitMillis);
	}

	@ServiceId("OpenStackTypica")
	public CloudInterface buildOpenStackTypica(
			@InjectService("PlainOpenStackTypica") CloudInterface openStack,
			@InjectService("NoDelay") DelayInjectionService addDelayBefore,
			@InjectService("NoDelay") DelayInjectionService addDelayAfter,
			@InjectService("CSVFileResourceMonitor") ResourceMonitor resourceMonitor)
			throws UnknownHostException {
		return new ResourceMonitoringCloudInterface(
				new PreDelayedCloudInterface(new PostDelayedCloudInterface(
						openStack, addDelayAfter), addDelayBefore),
				resourceMonitor);

	}

	public JEC2ClientFactory buildJEC2ClientFactory(Logger logger,
			@Symbol(OSSymbolConstants.OS_EC2_ACCESS_KEY) String accessKey,
			@Symbol(OSSymbolConstants.OS_EC2_SECRET_KEY) String secretKey,
			@Symbol(OSSymbolConstants.OS_EC2_CC_ADDRESS) String ccAddress,
			@Symbol(OSSymbolConstants.OS_EC2_CC_PORT) Integer ccPort,
			//
			@Symbol(OSSymbolConstants.CLIENT_SO_TIMEOUT) int soTimeout,
			@Symbol(OSSymbolConstants.CLIENT_CONNECTION_TIMEOUT) int connectionTimeout,
			@Symbol(OSSymbolConstants.CLIENT_CONNECTION_MANAGER_TIMEOUT) int connectionManagerTimeout,
			@Symbol(OSSymbolConstants.CLIENT_MAX_RETRIES) int maxRetries,
			@Symbol(OSSymbolConstants.CLIENT_MAX_CONNECTIONS) int maxConnections
	) {
		/*
		 * You should be aware when writing services that your code must be
		 * thread safe; any service you define could be invoked simultaneously
		 * by multiple threads. This is rarely an issue in practice, since most
		 * services take input, use local variables, and invoke methods on other
		 * services, without making use of non-final instance variables. The few
		 * instance variables in a service implementation are usually references
		 * to other Tapestry IoC services.
		 */
		return new JEC2ClientFactory(logger, accessKey, secretKey, ccAddress,
				ccPort, soTimeout, connectionTimeout, connectionManagerTimeout, maxRetries, maxConnections);
	}

	// /**
	// *
	// * If there is the specified configuration file we perform the
	// contribution,
	// * otherwise we simply skip it.
	// *
	// * If there specified file cannot be found we raise an exception
	// *
	// * @param configuration
	// * @param cloudPropertiesFileSymbolProvider
	// * @throws IOException
	// */
	// public static void contributeSymbolSource(
	// OrderedConfiguration<SymbolProvider> configuration,
	// // Not sure this is legal...
	// @InjectService("ApplicationDefaults") SymbolProvider applicationDefaults)
	// throws IOException {
	//
	// /*
	// * We cannot read symbols while we are building the symbol service so we
	// * need to check ENV, System.properties() and Application defaults
	// * MANUALLY in order to check if the specific keys are set.
	// *
	// * To recreate the original semantic we need to look for symbols in the
	// * following order: System, ENV, and eventually ApplicationDefaults
	// */
	// SymbolProvider systemProperties = new SystemPropertiesSymbolProvider();
	// SymbolProvider environmentVariables = new SystemEnvSymbolProvider();
	//
	// String configurationFile = systemProperties
	// .valueForSymbol(OSSymbolConstants.CONFIGURATION_FILE);
	//
	// if (configurationFile == null) {
	// configurationFile = environmentVariables
	// .valueForSymbol(OSSymbolConstants.CONFIGURATION_FILE);
	// }
	// if (configurationFile == null) {
	// configurationFile = applicationDefaults
	// .valueForSymbol(OSSymbolConstants.CONFIGURATION_FILE);
	// }
	//
	// if (configuration != null) {
	// if (new File(configurationFile).exists()) {
	// configuration.add("OSCloudPropertiesFileSymbolProvider",
	// new ConfigurationFileSymbolProvider(new File(
	// configurationFile).getAbsolutePath()),
	// "after:SystemProperties", "before:ApplicationDefaults");
	// }
	// }
	// }

	@Scope(value = ScopeConstants.PERTHREAD)
	@ServiceId("OSServiceDeployer")
	public static ServiceDeployer buildServiceDeployer(Logger logger,
			@InjectService("OSController") CloudController controller,
			@InjectService("OpenStackTypica") CloudInterface cloud) {

		return new OSServiceDeployer(logger, controller, cloud);
	}

	public static void contributeUserDataService(Logger logger,
			SymbolSource symbolSource,
			@InjectService("OpenStackTypica") CloudInterface cloud,
			OrderedConfiguration<UserDataServiceFilter> configuration) {

		configuration.add("OSPlatformUserDataService",
				new PlatformUserDataServiceFilter(logger, symbolSource), "");

		configuration.add("OSRemoteUserDataService",
				new RemoteValuesUserDataServiceFilter(logger, cloud), "");

	}

	@ServiceId("OSController")
	public static CloudController buildOSController(Logger logger,
			SymbolSource symbolSource,
			@InjectService("OpenStackTypica") CloudInterface cloud,
			UserDataService userDataService, InstanceService instanceService) {
		return new OSController(logger, symbolSource, cloud, userDataService,
				instanceService);
	}

}
