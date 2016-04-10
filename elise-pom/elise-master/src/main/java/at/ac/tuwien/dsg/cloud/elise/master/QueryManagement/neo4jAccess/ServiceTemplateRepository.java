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
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;


import at.ac.tuwien.dsg.cloud.elise.model.generic.ServiceUnit;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;


public interface ServiceTemplateRepository extends GraphRepository<ServiceUnit> {
//, RelationshipOperationsRepository<OfferedServiceUnit> {
        
        @Query("match (n:OfferedServiceUnit) return n")
	Set<ServiceUnit> listServiceUnit();
	
	@Query("match (n:OfferedServiceUnit) where n.name={name} return n")
	Set<ServiceUnit> findByName(@Param(value = "name") String name);
        
        @Query("match (n:OfferedServiceUnit) where n.id={id} return n")
	ServiceUnit findByUniqueID(@Param(value = "id") String id);
	
        @Query("match (p:Provider)-->(n:OfferedServiceUnit) where p.id={providerID} return n")
        Set<ServiceUnit> findByProviderID(@Param(value = "providerID") String providerID);        
        
	@Query("match (n:OfferedServiceUnit) where n.subcategory={subcategory} return n")
	Set<ServiceUnit> findBySubcategory(@Param(value = "subcategory") String subcategory);   
        	
	@Query("match (n) optional match (n)-[r]-() delete n,r")
	void cleanDataBase();
}
