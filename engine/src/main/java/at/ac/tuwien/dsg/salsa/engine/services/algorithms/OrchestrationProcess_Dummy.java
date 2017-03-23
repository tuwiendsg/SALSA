/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.database.neo4j.repo.CloudServiceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceInstanceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceUnitRepository;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvent;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class OrchestrationProcess_Dummy implements OrchestrationProcess {

    static Logger logger = LoggerFactory.getLogger("salsa");
    CloudServiceRepository cloudRepo;
    ServiceUnitRepository unitRepo;
    ServiceInstanceRepository instanceRepo;

    public OrchestrationProcess_Dummy(CloudServiceRepository cloudRepo, ServiceUnitRepository unitRepo, ServiceInstanceRepository instanceRepo) {
        this.cloudRepo = cloudRepo;
        this.unitRepo = unitRepo;
        this.instanceRepo = instanceRepo;
    }

    // for testing only, do nothing but return the service with all state are deployed
    @Override
    public void deployCloudservice(CloudService service) {
        logger.debug("Dummy orchestration, Only create data, do not deployed actually nothing...s");

        UnitCapabilityInterface unitCapa = new BaseUnitCapability(service.getName(), cloudRepo, instanceRepo);

        logger.debug("Start round check configuration");
        List<ServiceUnit> allUnits = service.getAllUnits();

        for (ServiceUnit unit : allUnits) {
            Date start = new Date();
            logger.debug("Create instance for unit: {}", unit.getName());
            ServiceInstance instance = new ServiceInstance();
            instance.setIndex(0);
            instance.setState(ConfigurationState.DEPLOYED);
            instance.setUuid(UUID.randomUUID().toString());
            instance.setHostedInstanceIndex(0);
            instance.setServiceUnitUuid(unit.getUuid());
            unit.hasInstance(instance);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            service.hasEvent(new SalsaEvent(unit.getName() + "-" + instance.getIndex(), start, new Date(), "deploy"));
        }
        cloudRepo.save(service);
        logger.debug("Saved service done. Now recheck ...");
        CloudService persistedService = cloudRepo.findByUuid(service.getUuid());
        logger.debug("Here we have: " + service.toJson());
        logger.debug("Dummy orchestration DONE !");
    }

}
