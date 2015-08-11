/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * The effect of a capability. This class contains a list of effect. Each contain a set of metric can change
 *
 * @author hungld
 */
@NodeEntity
public class CapabilityEffect {

    @GraphId
    Long graphID;

    /**
     * The ID of the unit which is affected. The unit can be topology, service unit or service instance
     */
    String targetUnitID;

    /**
     * This map contain pairs of <metric, effect>, e.g. <cpuUsage, -30.0>, <throughput, 200>
     */
    Map<String, Object> effects = new HashMap<>();
    //DynamicProperties effects = new DynamicPropertiesContainer();
    
    public CapabilityEffect() {
    }

    public CapabilityEffect(String targetUnitID) {
        this.targetUnitID = targetUnitID;
    }

    public CapabilityEffect hasEffect(String metric, Object change) {        
        this.effects.put(metric, change);
        return this;
    }
}
