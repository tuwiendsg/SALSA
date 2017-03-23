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
        System.out.println("Start saving CloudService to DB: " + service.getUuid() + ", " + service.getName());

        ODocument serviceDoc = serviceDAO.save(service);
        System.out.println("///////////////////////////////////");
        System.out.println("Saving CloudService without topos and units done: " + serviceDoc.toJSON());
        System.out.println("///////////////////////////////////");

        topoDAO.saveAll(service.getTopologies());
        unitDAO.saveAll(service.getAllUnits());

        long time2 = (new Date()).getTime();
        System.out.println("Saving to DB done, take: " + (((double) time2 - (double) time1) / 1000) + " seconds");
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
            }
            topo.setUnits(new HashSet<>(units));
        }
        service.setTopologies(new HashSet<>(topos));
        return service;
    }

    @Override
    public List<CloudService> readAll() {
        List<CloudService> services = serviceDAO.readAll();
        return services;
    }
}
