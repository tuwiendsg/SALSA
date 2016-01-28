/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.elise.model.generic;

import java.util.HashMap;
import java.util.Map;

/**
 * The effect of a capability. Each effect contains a set of metrics that can be changed due to a capability.
 *
 * @author Duc-Hung Le
 */
public class CapabilityEffect {

    private Long graphID;

    /**
     * The ID of the unit which is affected. The unit can be topology, service unit or service instance
     */
    protected String targetUnitID;

    /**
     * This map contain pairs of <metric, effect>, e.g. <cpuUsage, -30.0>, <throughput, 200>
     */
    protected Map<String, Object> effects = new HashMap<>();
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
