package at.ac.tuwien.dsg.salsa.database.orientdb.DAO;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hungld
 */
public class CloudServiceDAO extends AbstractDAO<CloudService> {

    AbstractDAO<CloudService> serviceDAO = new AbstractDAO(CloudService.class);
    AbstractDAO<ServiceTopology> topoDAO = new AbstractDAO<>(ServiceTopology.class);
    AbstractDAO<ServiceUnit> unitDAO = new AbstractDAO<>(ServiceUnit.class);
    AbstractDAO<ServiceInstance> instanceDAO = new AbstractDAO<>(ServiceInstance.class);

    public CloudServiceDAO() {
        super(CloudService.class);
    }

    @Override
    public ODocument save(CloudService service) {

        long time1 = (new Date()).getTime();
        logger.debug("Start saving CloudService to DB: " + service.getUuid() + ", " + service.getName());
        logger.debug(service.toJson());

        ODocument serviceDoc = serviceDAO.save(service);
        logger.debug("///////////////////////////////////");
        logger.debug("Saving CloudService without topos and units done: " + serviceDoc.toJSON());
        logger.debug("///////////////////////////////////");

        topoDAO.saveAll(service.getTopos());
        unitDAO.saveAll(service.getAllUnits());

        long time2 = (new Date()).getTime();
        logger.debug("Saving to DB done, take: " + (((double) time2 - (double) time1) / 1000) + " seconds");
        return serviceDoc;
    }

    @Override
    public List<ODocument> saveAll(Collection<CloudService> objects) {
        if (objects == null) {
            return null;
        }
        List<ODocument> docs = new ArrayList<>();
        for (CloudService service : objects) {
            docs.add(save(service));
        }
        return docs;
    }

    @Override
    public CloudService read(String uuid) {
        CloudService service = (CloudService) serviceDAO.read(uuid);
        // read all the service topology belong to the service        
        String whereCondition = "cloudServiceUUID='" + uuid + "'";
        List<ServiceTopology> topos = topoDAO.readWithCondition(whereCondition);
        for (ServiceTopology topo : topos) {
            String unitWhereCondition = "topologyUuid='" + topo.getUuid() + "'";
            List<ServiceUnit> units = unitDAO.readWithCondition(unitWhereCondition);
            for (ServiceUnit unit : units) {
                String instanceWhereCondition = "serviceUnitUuid='" + unit.getUuid() + "'";
                List<ServiceInstance> instances = instanceDAO.readWithCondition(instanceWhereCondition);
                unit.setInstances(new HashSet<>(instances));
                topo.hasUnit(unit);
            }
        }
        service.setTopos(new HashSet<>(topos));
        return service;
    }

    @Override
    public List<CloudService> readAll() {
        List<CloudService> services = serviceDAO.readAll();
        List<CloudService> result = new ArrayList<>();
        for (CloudService s : services) {
            result.add(this.read(s.getUuid()));
        }
        return result;
    }

    public CloudService readByName(String name) {
        List<CloudService> services = serviceDAO.readWithCondition("name='" + name + "'");
        if (services == null || services.isEmpty()) {
            return null;
        }
        CloudService service = (CloudService) serviceDAO.readWithCondition("name='" + name + "'").get(0);
        String uuid = service.getUuid();
        // read all the service topology belong to the service        
        String whereCondition = "cloudServiceUUID='" + uuid + "'";
        List<ServiceTopology> topos = topoDAO.readWithCondition(whereCondition);
        for (ServiceTopology topo : topos) {
            String unitWhereCondition = "topologyUuid='" + topo.getUuid() + "'";
            List<ServiceUnit> units = unitDAO.readWithCondition(unitWhereCondition);
            for (ServiceUnit unit : units) {
                String instanceWhereCondition = "serviceUnitUuid='" + unit.getUuid() + "'";
                List<ServiceInstance> instances = instanceDAO.readWithCondition(instanceWhereCondition);
                unit.setInstances(new HashSet<>(instances));
                topo.hasUnit(unit);
            }
        }
        service.setTopos(new HashSet<>(topos));
        return service;
    }
}
