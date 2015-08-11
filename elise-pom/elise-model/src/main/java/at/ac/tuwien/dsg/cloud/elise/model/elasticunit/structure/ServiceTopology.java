package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.NodeEntity;

//@NodeEntity
public class ServiceTopology extends ServiceEntity {

//    @GraphId
//    Long graphID;

    protected Set<ServiceUnit> serviceUnits = new HashSet<>();
    protected Set<ServiceTopology> serviceTopologies = new HashSet<>();

    public ServiceTopology() {
    }

    public ServiceTopology(String id) {
        super(id);
    }

    public ServiceTopology(
            String id,
            Set<ServiceUnit> serviceUnits,
            Set<ServiceTopology> serviceTopologies) {
        this.serviceUnits = serviceUnits;
        this.serviceTopologies = serviceTopologies;
    }

    public void addServiceUnit(ServiceUnit serviceUnit) {
        if (serviceUnits == null) {
            serviceUnits = new HashSet<>();
        }
        serviceUnits.add(serviceUnit);
    }

    public void addTopology(ServiceTopology serviceTopology) {
        if (serviceTopologies == null) {
            serviceTopologies = new HashSet<>();
        }
        serviceTopologies.add(serviceTopology);
    }

    public List<ServiceTopology> getServiceTopologiesList() {
        return new ArrayList<>(serviceTopologies);
    }

    public List<ServiceUnit> getServiceUnitsList() {
        return new ArrayList<>(serviceUnits);
    }

    public List<ServiceUnit> getServiceUnitList() {
        return new ArrayList<>(serviceUnits);
    }

	// GENERATED METHODS
    public Set<ServiceTopology> getServiceTopologies() {
        return serviceTopologies;
    }

    public Set<ServiceUnit> getServiceUnits() {
        return serviceUnits;
    }

    public void setServiceUnits(Set<ServiceUnit> serviceUnits) {
        this.serviceUnits = serviceUnits;
    }

    public void setServiceTopologies(Set<ServiceTopology> serviceTopologies) {
        this.serviceTopologies = serviceTopologies;
    }

}
