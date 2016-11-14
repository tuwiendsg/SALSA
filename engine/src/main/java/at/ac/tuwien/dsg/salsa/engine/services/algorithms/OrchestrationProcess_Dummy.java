/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class OrchestrationProcess_Dummy implements OrchestrationProcess {

    static Logger logger = LoggerFactory.getLogger("salsa");

    // for testing only, do nothing but return the service with all state are deployed
    @Override
    public void deployCloudservice(CloudService service) {

        logger.debug("Dummy orchestration, doing nothing...s");
    }

}
