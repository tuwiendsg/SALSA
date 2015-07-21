package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;

public class WarInstrument implements ArtifactConfigurationInterface {

    static Logger logger = PioneerConfiguration.logger;

    @Override
    public SalsaMsgConfigureState configureArtifact(SalsaMsgConfigureArtifact configInfo) {
        String webContainerDir = "/var/lib/tomcat7/webapps/";
        logger.debug("Copying war file to the target. File: " + configInfo.getRunByMe());
        try {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(PioneerConfiguration.getWorkingDirOfInstance(configInfo.getUnit(), configInfo.getInstance()) + "/" + configInfo.getRunByMe());
                output = new FileOutputStream(webContainerDir + configInfo.getRunByMe());
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            } finally {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getStatus(SalsaMsgConfigureArtifact configInfo) {
        logger.error("Error: Not support yet !");
        return "Error: not support";
    }

}
