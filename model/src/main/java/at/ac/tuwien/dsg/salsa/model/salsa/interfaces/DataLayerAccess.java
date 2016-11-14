/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.interfaces;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import java.util.List;

/**
 *
 * @author hungld
 */
public interface DataLayerAccess {
    /////
    // For cloud service
    /////

    public CloudService readCloudServiceByName(String name);

    public CloudService readCloudServiceByUuid(String uuid);

    public List<String> readcloudSErviceNames();

    public List<String> readCloudServicesUuids();

    public CloudService writeCloudService(CloudService service);

    /////
    // For service topology
    /////
    public ServiceTopology readTopologyByUuid(String uuid);

    public ServiceTopology writeTopology(ServiceTopology topology);

    /////
    // For service unit
    /////
    public ServiceUnit readUnitByUuid(String uuid);

    public ServiceUnit writeUnit(ServiceUnit unit);

    /////
    // For service instance
    /////
    public ServiceInstance readInstanceByUuid(String uuid);

    public ServiceInstance writInstance(ServiceInstance instance);

}
