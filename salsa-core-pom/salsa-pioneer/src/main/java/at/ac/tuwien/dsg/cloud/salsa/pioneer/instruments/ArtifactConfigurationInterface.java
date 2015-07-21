package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;


public interface ArtifactConfigurationInterface {

    /**
     * Configure an artifact
     *
     * @param configInfo The configuration info
     * @return The state of the configuration either successful or error
     */
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo);

    /**
     * Define how to get status of a running instance. E.g, with system service-based, state can be get via "system serviceName status"
     *
     * @param configInfo The info of the instance
     * @return A custom description of the status
     */
    public String getStatus(SalsaMsgConfigureArtifact configInfo);

  
}
