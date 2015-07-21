package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import org.slf4j.Logger;

public class AptGetInstrument implements ArtifactConfigurationInterface {

    static Logger logger = PioneerConfiguration.logger;

    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {
        Process p;
        logger.debug("apt-get the artifact name: " + configInfo.getRunByMe());
        int returnCode = SystemFunctions.executeCommandGetReturnCode("apt-get -y install " + configInfo.getRunByMe(), null, null);
        if (returnCode == 0) {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, returnCode, "Apt-get installed package successfully !");
        } else {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, returnCode, "Apt-get installed package failed !");
        }
    }

    // TODO: refine this
    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        return SystemFunctions.executeCommandGetOutput("dpkg -l " + configInfo.getRunByMe(), "/tmp", null);
    }

}
