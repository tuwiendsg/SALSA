/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability;

import at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource.PhysicalResource;
import java.util.HashMap;
import java.util.Map;

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
    
    public CapabilityEffect(PhysicalResource entity, String attribute, String effect){
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
