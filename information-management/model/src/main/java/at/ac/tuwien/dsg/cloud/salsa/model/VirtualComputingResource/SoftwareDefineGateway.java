package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource;

import at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource.PhysicalResource;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream.DataStream;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import java.util.ArrayList;
import java.util.HashMap;

public class SoftwareDefineGateway {

    /**
     * The uuid is unique within the whole system
     */
    private String uuid;

    /**
     * The name for human reading purpose
     */
    private String name;

    /**
     * The List of control capabilities and data streams
     */
    private List<Capability> capabilities;
//    private List<DataStream> dataStreams;

    /**
     * The physical resource give info. of what Things this SDG manages
     */
    private List<PhysicalResource> physicalResources;

    /**
     * For custom data, e.g. created date, position, comments
     */
    private Map<String, String> meta;

    /**
     * Construction and get/set
     */
    public SoftwareDefineGateway() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Capability> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<>();
        }
        return capabilities;
    }

    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }

//    public List<DataStream> getDataStreams() {
//        return dataStreams;
//    }
//
//    public void setDataStreams(List<DataStream> dataStreams) {
//        this.dataStreams = dataStreams;
//    }
    public List<PhysicalResource> getPhysicalResources() {
        return physicalResources;
    }

    public void setPhysicalResources(List<PhysicalResource> physicalResources) {
        this.physicalResources = physicalResources;
    }

    public Map<String, String> getMeta() {
        if (meta == null) {
            meta = new HashMap<>();
        }
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    /**
     *
     * @author hungld
     */
    public class CapabilityEffect {

        /**
         * The id of the physical resource is affect
         */
        private PhysicalResource affectedEntity;

        /**
         * This show a list of effect that change the resource attribute, e.g. [sensorRate,+1] or [connectProtocol,mqtt]
         */
        private Map<String, String> effects = new HashMap<>();

        public CapabilityEffect() {
        }

        public CapabilityEffect(PhysicalResource entity, String attribute, String effect) {
            this.affectedEntity = entity;
            effects.put(attribute, effect);
        }

        public PhysicalResource getAffectedEntity() {
            return affectedEntity;
        }

        public void setAffectedEntity(PhysicalResource affectedEntity) {
            this.affectedEntity = affectedEntity;
        }

        public Map<String, String> getEffects() {
            return effects;
        }

        public void setEffects(Map<String, String> effects) {
            this.effects = effects;
        }

    }
}
