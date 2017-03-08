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

import at.ac.tuwien.dsg.salsa.model.CloudService;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Duc-Hung Le
 */
public interface CloudServiceRepository extends GraphRepository<CloudService> {

    @Query("match (n:CloudService) return n")
    Set<CloudService> findAllService();

    @Query("match (n:CloudService) return n.name")
    Set<String> findAllServiceName();

    @Query("match (n:CloudService) where n.uuid={uuid} return n")
    CloudService findByUuid(@Param(value = "uuid") String uuid);

    @Query("match (n:CloudService) where n.name={name} return n")
    CloudService findByName(@Param(value = "name") String name);

    @Query("match (n:CloudService)-[*]->x where n.id={id} WITH x MATCH x-[r]-() delete x,r")
    Set<CloudService> deleteUnitByID(@Param(value = "id") String id);

}
