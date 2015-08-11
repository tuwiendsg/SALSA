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
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaMsgConfigureArtifact {

    public static void main(String[] args) {
        String data = "{\"actionID\":\"12f1033d-9c29-453d-aa78-4ebc1da8c3bd\",\"actionName\":\"deploy\",\"user\":\"salsa-default\",\"service\":\"test\",\"unit\":\"helloWorld\",\"instance\":1,\"unitType\":\"ExecutableApp\",\"runByMe\":\"\",\"artifactType\":\"misc\",\"artifacts\":[{\"name\":\"Deployment script\",\"type\":\"sh\",\"reference\":\"http://128.130.172.215/salsa/upload/files/daas/fakescripts/donothing.sh\"}]}";
        SalsaMsgConfigureArtifact confInfo = SalsaMsgConfigureArtifact.fromJson(data);
        System.out.println(confInfo.getActionID());
    }

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
    ServiceCategory unitType;   // see ServiceCategory, for managing

    // how to configure. RunByMe simply a list of command, which separate by ";"
    String preRunByMe;
    String runByMe;
    SalsaArtifactType artifactType;    // e.g. Bash, bash continuous, chef solo, apt-get etc. For configuring artifact.
    String environment;
    List<DeploymentArtifact> artifacts;

    public enum ConfigurationModules {

        APT_GET,
        BASH,
        BASH_CONTINUOUS,
        WAR,
        DOCKER,
    }

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

    public SalsaMsgConfigureArtifact() {
    }

    public SalsaMsgConfigureArtifact(String actionID, String actionName, String pioneerID, String user, String service, String topology, String unit, int instance, ServiceCategory unitType, String preRunByMe, String runByMe, SalsaArtifactType artifactType, String environment) {
        this.actionID = actionID;
        this.actionName = actionName;
        this.pioneerID = pioneerID;
        this.user = user;
        this.service = service;
        this.topology = topology;
        this.unit = unit;
        this.instance = instance;
        this.unitType = unitType;
        this.preRunByMe = preRunByMe;
        this.runByMe = runByMe;
        this.artifactType = artifactType;
        this.environment = environment;
    }

    public SalsaMsgConfigureArtifact hasArtifact(String name, String type, String reference) {
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

    public static SalsaMsgConfigureArtifact fromJson(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(payload, SalsaMsgConfigureArtifact.class);
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

    public ServiceCategory getUnitType() {
        return unitType;
    }

    public String getRunByMe() {
        return runByMe;
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

    public String getPreRunByMe() {
        return preRunByMe;
    }

    // need these :)
    public void setRunByMe(String runByMe) {
        this.runByMe = runByMe;
    }

    public void setPreRunByMe(String preRunByMe) {
        this.preRunByMe = preRunByMe;
    }

    public String getEnvironment() {
        return environment;
    }

}
