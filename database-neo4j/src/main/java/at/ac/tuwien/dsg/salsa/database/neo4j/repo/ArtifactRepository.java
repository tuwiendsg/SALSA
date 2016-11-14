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
package at.ac.tuwien.dsg.salsa.database.neo4j.repo;

import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepository extends GraphRepository<Artifact> {
//, RelationshipOperationsRepository<OfferedServiceUnit> {

    @Query("match (n:Artifact) return n")
    Set<Artifact> listArtifact();
    
    @Query("match (n:Artifact) where n.name={name} and n.version={version} and n.type={type} return n")
    Set<Artifact> findByAttributes(@Param(value = "name") String name, @Param(value = "version") String version, @Param(value = "type") String type);

    @Query("match (n:Artifact) where n.name={name} return n")
    Set<Artifact> findByName(@Param(value = "name") String name);

}
