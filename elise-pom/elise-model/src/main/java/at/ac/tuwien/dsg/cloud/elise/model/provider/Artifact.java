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

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.ExtensibleModel;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Refer to different kinds of artifact. Currently this model support only direct URL download style. Note: the artifact have no UUID, we identify it by all the
 * attributes.
 *
 * @author Duc-Hung LE
 */
@NodeEntity
public class Artifact extends ExtensibleModel{

    @GraphId
    Long graphID;

    protected String name;
    protected String version;
    protected SalsaArtifactType type;
    protected String reference;

    public Artifact() {
        super(Artifact.class);
    }

    public Artifact(String name, SalsaArtifactType type, String version, String reference) {
        super(Artifact.class);
        this.name = name;
        this.type = type;
        this.version = version;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public SalsaArtifactType getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
