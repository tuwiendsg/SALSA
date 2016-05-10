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
package at.ac.tuwien.dsg.cloud.elise.model.provider;

import at.ac.tuwien.dsg.cloud.elise.model.generic.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Describe a service unit provided by cloud provider. The service need to be initiated to run.
 *
 * @author Duc-Hung LE
 */
@NodeEntity
public class ServiceTemplate extends ServiceUnit {

    protected String providerID;
    @RelatedTo
    @Fetch
    protected Set<Artifact> artifacts = new HashSet<>();

    public ServiceTemplate() {
    }

    public ServiceTemplate(String name, ServiceCategory category) {
        super(name, category);
    }

    public ServiceTemplate(String name, ServiceCategory category, String providerID) {
        super(name, category);
        this.providerID = providerID;
    }

    public String getProviderID() {
        return this.providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public ServiceUnit hasArtifact(Artifact artifact) {
        this.artifacts.add(artifact);
        return this;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public Artifact findArtifactByName(String name) {
        for (Artifact art : this.artifacts) {
            if (art.getName().equals(name)) {
                return art;
            }
        }
        return null;
    }

}
