/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.database.neo4j.repo.CloudServiceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceInstanceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceUnitRepository;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This algorithm is traditional process of Salsa.
 *
 * - All the units of the cloudservice are put into a list. - The process check
 * the unit one by one, and repeat. - If all the dependencies are fulfilled, the
 * deployment is call.
 *
 * @author hungld
 */
public class OrchestrationProcess_RoundCheck implements OrchestrationProcess {

    static Logger logger = LoggerFactory.getLogger("salsa");
    CloudService cloudService;

    // Map store service unit uuid --> number of instances to be deployed
    Map<ServiceUnit, Integer> instancesNeeded = new HashMap<>();
    int cycle = 0;

    CloudServiceRepository cloudRepo;
    ServiceUnitRepository unitRepo;
    ServiceInstanceRepository instanceRepo;

    public OrchestrationProcess_RoundCheck(CloudServiceRepository cloudRepo, ServiceUnitRepository unitRepo, ServiceInstanceRepository instanceRepo) {
        this.cloudRepo = cloudRepo;
        this.unitRepo = unitRepo;
        this.instanceRepo = instanceRepo;
    }

    @Override
    public void deployCloudservice(CloudService service) {
        UnitCapabilityInterface unitCapa = new BaseUnitCapability(service.getName(), cloudRepo, instanceRepo);

        logger.debug("Start round check configuration");
        this.cloudService = service;
        List<ServiceUnit> allUnits = service.getAllComponent();

        // build the list of needed instances
        int remainSteps = 0;
        for (ServiceUnit unit : allUnits) {
            instancesNeeded.put(unit, unit.getMin());
            remainSteps += unit.getMin();
        }

        while (remainSteps > 0) {
            logOrchestrationStatus();
            for (ServiceUnit unit : instancesNeeded.keySet()) {
                if (instancesNeeded.get(unit) > 0) {
                    try {
                        ServiceInstance instance = unitCapa.deploy(service.getName(), unit.getName());
                        if (instance != null) {
                            remainSteps = remainSteps - 1;
                            logger.debug("Orchestration is done for unit: {}/{}, {} steps left", cloudService.getName(), unit.getName(), remainSteps);
                            instancesNeeded.put(unit, instancesNeeded.get(unit) - 1);
                        } else {
                            logger.debug("This round, unit {}/{}", service.getName(), unit.getName());
                        }
                    } catch (SalsaException ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        logger.debug("Rough check orchestation has finished !");
    }

    private void logOrchestrationStatus() {
        logger.debug("============================");
        logger.debug("Orchestration service: " + cloudService.getName() + ": " + cycle++);
        StringBuffer queue = new StringBuffer();
        for (Map.Entry<ServiceUnit, Integer> entry : instancesNeeded.entrySet()) {
            queue.append(entry.getKey().getName()).append(":").append(entry.getValue()).append(" -- ");
        }

        logger.debug(" - Instance queue: " + queue.toString());
        logger.debug(" - Pioneer list: " + PioneerManager.describe());

    }
}
