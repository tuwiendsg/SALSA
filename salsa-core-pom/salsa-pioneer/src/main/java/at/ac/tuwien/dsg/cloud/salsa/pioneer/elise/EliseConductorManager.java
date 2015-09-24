/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.elise;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung LE
 */
public class EliseConductorManager {

    static Logger logger = PioneerConfiguration.logger;

    /**
     * Run a conductor if need
     *
     * @return ID of the conductor
     */
    public static String runConductor() {
        try {
            InputStream is = EliseConductorManager.class.getResourceAsStream("/scripts/elise_conductor_install.sh");
            OutputStream os = new FileOutputStream(new File(PioneerConfiguration.getWorkingDir() + "/elise_conductor_install.sh"));
            IOUtils.copy(is, os);
            int result = SystemFunctions.executeCommandGetReturnCode("/bin/bash " + PioneerConfiguration.getWorkingDir() + "/elise_conductor_install.sh " + PioneerConfiguration.getEliseConductorURL(), PioneerConfiguration.getWorkingDir() , PioneerConfiguration.getPioneerID());            
            return result + "";
        } catch (MalformedURLException ex) {
            logger.error("Wrong conductor URL", ex);
        } catch (IOException ex) {
            logger.error("Cannot download conductor artifact", ex);
        }
        return null;
    }
    
    
    /**
     * Inject a collector
     * @param collectorName
     * @return 
     */
    public static String injectCollectors(String collectorName){
        return "Will be implemented";
    }
}
