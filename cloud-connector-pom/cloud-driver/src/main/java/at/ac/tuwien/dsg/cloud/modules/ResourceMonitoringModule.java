package at.ac.tuwien.dsg.cloud.modules;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.services.ResourceMonitor;
import at.ac.tuwien.dsg.cloud.services.impl.ResourceMonitorImpl;
import at.ac.tuwien.dsg.cloud.utils.CloudSymbolConstants;

public class ResourceMonitoringModule {

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(CloudSymbolConstants.RESOURCE_MONITORING_FILE,
				"resources.csv");
	}

	public ResourceMonitor buildCSVFileResourceMonitor(
			Logger logger,
			@Symbol(CloudSymbolConstants.RESOURCE_MONITORING_FILE) File outputFile,
			RegistryShutdownHub registryShutdownHub) throws IOException {

		if (!outputFile.exists()) {
			logger.warn("ResourceMonitoringModule.buildCSVFileResourceMonitor() outputfile does not exists !");
			outputFile.createNewFile();
		}

		return new ResourceMonitorImpl(logger, outputFile, registryShutdownHub);

	}
}
