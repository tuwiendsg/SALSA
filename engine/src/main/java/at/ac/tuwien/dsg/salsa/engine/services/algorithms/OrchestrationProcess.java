/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.model.CloudService;

/**
 * This abstract class is used for the Thread to run the algorithm. Other class who
 * implement this just do the deployment of the cloud service from the input.
 *
 * @author hungld
 */
public interface OrchestrationProcess {

    /**
     * This function will be implemented for orchestration algorithms. The input
     * will be a cloud service with topology and units. The algorithms must
     * manage the deployment state by itself.
     *
     * @param service
     */
    public abstract void deployCloudservice(CloudService service);
}
