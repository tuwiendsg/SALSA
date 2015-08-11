package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.relationship.ConnectToRel;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.relationship.HostOnRel;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.relationship.LocalRel;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;

//@NodeEntity
public class ServiceUnit extends ServiceEntity {

    protected Integer minInstances = 1;

    protected Integer maxInstances = 1;

    protected Boolean elasticUnit;

   // @RelatedToVia(type = "HOST_ON")
    protected HostOnRel host;

   // @RelatedToVia(type = "CONNECT_TO")
    protected Set<ConnectToRel> connectTo = new HashSet<>();

   // @RelatedToVia(type = "LOCAL")
    protected Set<LocalRel> local = new HashSet<>();

    protected Set<UnitInstance> instances = new HashSet<>();

    public ServiceUnit() {
        super();
    }

    public ServiceUnit(String id) {
        this.id = id;
    }

    public ServiceUnit(String id, String name, Integer minInstances,
            Integer maxInstances) {
        super(id, name);
        this.minInstances = minInstances;
        this.maxInstances = maxInstances;
    }

    public void addConnectTo(ConnectToRel rel) {
        if (connectTo == null) {
            connectTo = new HashSet<>();
        }
        connectTo.add(rel);
    }

    public void addLocal(LocalRel rel) {
        if (local == null) {
            local = new HashSet<>();
        }
        local.add(rel);
    }

    public void addUnitInstance(UnitInstance instance) {
        if (instances == null) {
            instances = new HashSet<>();
        }
        instances.add(instance);
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ConnectToRel> getConnectToList() {
        return new ArrayList<ConnectToRel>(connectTo);
    }

    // GENERATED METHODS
    public Integer getMinInstances() {
        return minInstances;
    }

    public void setMinInstances(Integer minInstances) {
        this.minInstances = minInstances;
    }

    public Integer getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(Integer maxInstances) {
        this.maxInstances = maxInstances;
    }

    public HostOnRel getHost() {
        return host;
    }

    public void setHost(HostOnRel hostNode) {
        this.host = hostNode;
    }

    public Set<ConnectToRel> getConnectTo() {
        return connectTo;
    }

    public void setConnectTo(Set<ConnectToRel> connectTo) {
        this.connectTo = connectTo;
    }

    public Set<LocalRel> getLocal() {
        return local;
    }

    public void setLocal(Set<LocalRel> local) {
        this.local = local;
    }

    public Set<UnitInstance> getInstances() {
        return instances;
    }

    public void setInstances(Set<UnitInstance> instances) {
        this.instances = instances;
    }

    /**
     * Whether it is also a unit in sense of rSYBL and MELA
     *
     * @return
     */
    public Boolean getElasticUnit() {
        return elasticUnit;
    }

    public void setElasticUnit(Boolean elasticUnit) {
        this.elasticUnit = elasticUnit;
    }

}
