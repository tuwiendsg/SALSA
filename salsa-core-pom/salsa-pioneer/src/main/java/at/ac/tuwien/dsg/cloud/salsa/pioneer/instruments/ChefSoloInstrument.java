package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import static at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions.executeCommandGetOutput;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ChefSoloInstrument implements ArtifactConfigurationInterface {

    static Logger logger = PioneerConfiguration.logger;

	// REPO: https://github.com/pjungwir/cookbooks
    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {
        URL inputUrl = SystemFunctions.class.getResource("/scripts/chef_solo_install.sh");
        File dest = new File(PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance())+"/chef_solo_install.sh");
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int returnCode = SystemFunctions.executeCommandGetReturnCode("/bin/bash ./chef_solo_install.sh " + configInfo.getRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance()), configInfo.getActionID());
         if (returnCode == 0){
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, returnCode, "Configure by ChefSolo is DONE: " + configInfo.getRunByMe());
        } else {
            return new SalsaMsgConfigureState(configInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, returnCode, "Configure by ChefSolo is FAILED: " + configInfo.getRunByMe());
        }   
    }

    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        return "Not supported yet !";
    }

}
