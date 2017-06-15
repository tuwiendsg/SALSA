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
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaConfigureTask {

    // to identify an configuration action and who will execute it
    String actionID;
    String actionName;
    String pioneerID; // the one who will execute the action

    // metadata of the action
    String user;
    String service;
    String topology;
    String unit;
    int instance;
    String unitType;   // see ServiceCategory, for managing

    // parameters contain all needed parameters for specific artifact type
    Map<String, String> parameters;
    SalsaArtifactType artifactType;    // e.g. Bash, bash continuous, chef solo, apt-get etc. For configuring artifact.
    String environment;
    List<DeploymentArtifact> artifacts;

    // what is required
    public static class DeploymentArtifact {

        String name;
        String type;
        String reference;

        public DeploymentArtifact() {
        }

        public DeploymentArtifact(String name, String type, String reference) {
            this.name = name;
            this.type = type;
            this.reference = reference;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getReference() {
            return reference;
        }
    }

    public SalsaConfigureTask() {
    }

    public SalsaConfigureTask(String actionName, String pioneerID, String user, String service, String topology, String unit, int instance, SalsaArtifactType artifactType) {
        this.actionID = UUID.randomUUID().toString();
        this.actionName = actionName;
        this.pioneerID = pioneerID;
        this.user = user;
        this.service = service;
        this.topology = topology;
        this.unit = unit;
        this.instance = instance;
        this.artifactType = artifactType;
    }

    public SalsaConfigureTask hasActionId(String actionId) {
        this.actionID = actionId;
        return this;
    }

    public SalsaConfigureTask hasActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    public SalsaConfigureTask hasPioneerUUID(String pioneeruuid) {
        this.pioneerID = pioneeruuid;
        return this;
    }

    public SalsaConfigureTask hasUser(String user) {
        this.user = user;
        return this;
    }

    public SalsaConfigureTask hasServiceName(String serviceName) {
        this.service = serviceName;
        return this;
    }

    public SalsaConfigureTask hasTopologyName(String toponame) {
        this.topology = toponame;
        return this;
    }

    public SalsaConfigureTask hasUnitName(String unitName) {
        this.unit = unitName;
        return this;
    }

    public SalsaConfigureTask hasInstanceIndex(int index) {
        this.instance = index;
        return this;
    }

    public SalsaConfigureTask hasUnitType(String unitType) {
        this.unitType = unitType;
        return this;
    }

    public SalsaConfigureTask hasArtifactType(SalsaArtifactType artType) {
        this.artifactType = artType;
        return this;
    }

    public SalsaConfigureTask hasEnvironment(String env) {
        this.environment = env;
        return this;
    }

    public SalsaConfigureTask hasParam(String name, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(name, value);
        return this;
    }

    public SalsaConfigureTask hasArtifact(String name, String type, String reference) {
        if (this.artifacts == null) {
            this.artifacts = new ArrayList<>();
        }
        this.artifacts.add(new DeploymentArtifact(name, type, reference));
        return this;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SalsaConfigureTask fromJson(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(payload, SalsaConfigureTask.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getActionID() {
        return actionID;
    }

    public String getUser() {
        return user;
    }

    public String getPioneerID() {
        return pioneerID;
    }

    public String getService() {
        return service;
    }

    public String getUnit() {
        return unit;
    }

    public int getInstance() {
        return instance;
    }

    public String getUnitType() {
        return unitType;
    }

    public List<DeploymentArtifact> getArtifacts() {
        return artifacts;
    }

    public String getActionName() {
        return actionName;
    }

    public SalsaArtifactType getArtifactType() {
        return artifactType;
    }

    public String getTopology() {
        return topology;
    }

    public String getEnvironment() {
        return environment;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameters(String key) {
        if (this.parameters == null) {
            return null;
        }
        return this.parameters.get(key);
    }

}
