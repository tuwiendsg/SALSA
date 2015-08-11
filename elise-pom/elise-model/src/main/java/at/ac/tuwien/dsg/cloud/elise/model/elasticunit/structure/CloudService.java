package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

//@NodeEntity
public class CloudService extends ServiceEntity {

//    @GraphId
//    Long graphID;

    protected String accessIp;

    protected Long dateCreated;

    protected Set<ServiceTopology> serviceTopologies = new HashSet<>();

    public CloudService() {
    }

    public CloudService(String id) {
        super(id);
    }

    public CloudService(String id, String name, Long dateCreated) {
        super(id, name);
        this.dateCreated = dateCreated;
    }

    public void addServiceTopology(ServiceTopology serviceTopology) {
        if (serviceTopologies == null) {
            serviceTopologies = new HashSet<>();
        }
        serviceTopologies.add(serviceTopology);
    }

    public List<ServiceTopology> getServiceTopologiesList() {
        return new ArrayList<>(serviceTopologies);
    }

	// GENERATED METHODS
    public Set<ServiceTopology> getServiceTopologies() {
        return serviceTopologies;
    }

    public void setServiceTopologies(Set<ServiceTopology> serviceTopologies) {
        this.serviceTopologies = serviceTopologies;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    

}
