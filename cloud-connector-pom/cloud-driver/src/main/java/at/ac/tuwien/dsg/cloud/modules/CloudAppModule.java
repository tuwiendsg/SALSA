package at.ac.tuwien.dsg.cloud.modules;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.internal.services.SystemEnvSymbolProvider;
import org.apache.tapestry5.ioc.internal.services.SystemPropertiesSymbolProvider;
import org.apache.tapestry5.ioc.services.PipelineBuilder;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.services.DelayInjectionService;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import at.ac.tuwien.dsg.cloud.services.UserDataServiceFilter;
import at.ac.tuwien.dsg.cloud.services.impl.ConfigurationFileSymbolProvider;
import at.ac.tuwien.dsg.cloud.services.impl.InstanceServiceImpl;
import at.ac.tuwien.dsg.cloud.services.impl.RuntimeUserData;
import at.ac.tuwien.dsg.cloud.utils.CloudSymbolConstants;

@SubModule(ResourceMonitoringModule.class)
public class CloudAppModule {

	/**
	 * 
	 * If there is the specified configuration file we perform the contribution,
	 * otherwise we simply skip it.
	 * 
	 * If there specified file cannot be found we raise an exception
	 * 
	 * @param configuration
	 * @param cloudPropertiesFileSymbolProvider
	 * @throws IOException
	 */
	public static void contributeSymbolSource(
			OrderedConfiguration<SymbolProvider> configuration,
			// Not sure this is legal...
			@InjectService("ApplicationDefaults") SymbolProvider applicationDefaults)
			throws IOException {

		/*
		 * We cannot read symbols while we are building the symbol service so we
		 * need to check ENV, System.properties() and Application defaults
		 * MANUALLY in order to check if the specific keys are set.
		 * 
		 * To recreate the original semantic we need to look for symbols in the
		 * following order: System, ENV, and eventually ApplicationDefaults
		 */
		SymbolProvider systemProperties = new SystemPropertiesSymbolProvider();
		SymbolProvider environmentVariables = new SystemEnvSymbolProvider();

		String configurationFile = systemProperties
				.valueForSymbol(CloudSymbolConstants.CONFIGURATION_FILE);

		if (configurationFile == null) {
			configurationFile = environmentVariables
					.valueForSymbol(CloudSymbolConstants.CONFIGURATION_FILE);
		}
		if (configurationFile == null) {
			configurationFile = applicationDefaults
					.valueForSymbol(CloudSymbolConstants.CONFIGURATION_FILE);
		}

		if (configuration != null) {
			if (new File(configurationFile).exists()) {
				configuration.add("CloudPropertiesFileSymbolProvider",
						new ConfigurationFileSymbolProvider(new File(
								configurationFile).getAbsolutePath()),
						"after:SystemProperties", "before:ApplicationDefaults");
			}
		}	
	}

	public DelayInjectionService buildNoDelay() {
		return new DelayInjectionService() {
			@Override
			public void injectDelay() {
				return;
			}
		};
	}

	/**
	 * This service mimic the Original Environmental service ... maybe we need
	 * to invoke something like, clear at the end ? Anyway it is supposed to be
	 * an internal service
	 * 
	 * @param logger
	 * @return
	 */
	@Scope(ScopeConstants.PERTHREAD)
	public static InstanceService build(Logger logger) {
		return new InstanceServiceImpl(logger);
	}

	@Scope(ScopeConstants.PERTHREAD)
	public static UserDataService build(
			@InjectService("PipelineBuilder") PipelineBuilder builder,
			List<UserDataServiceFilter> configuration, Logger logger) {

		// The terminator data service is the one that injects the instance
		// values!
		UserDataService terminator = new RuntimeUserData(logger);

		return builder.build(logger, UserDataService.class,
				UserDataServiceFilter.class, configuration, terminator);
	}
}
