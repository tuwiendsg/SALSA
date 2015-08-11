/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;
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
