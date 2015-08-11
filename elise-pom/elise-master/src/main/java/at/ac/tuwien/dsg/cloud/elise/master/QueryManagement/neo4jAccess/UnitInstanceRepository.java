/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Duc-Hung Le
 */

public interface UnitInstanceRepository extends GraphRepository<UnitInstance> {

    @Query("match (n:UnitInstance) return n")
    Set<UnitInstance> listUnitInstance();

    @Query("match (n:UnitInstance) where n.id={id} return n")
    UnitInstance findByUniqueID(@Param(value = "id") String id);

    @Query("match (n:UnitInstance) where n.name={name} return n")
    UnitInstance findByName(@Param(value = "name") String name);

    @Query("match (n:UnitInstance) where n.category={category} return n")
    Set<UnitInstance> findByCategory(@Param(value = "category") String category);

    @Query("match (n:UnitInstance)-[*]->x where n.id={id} WITH x MATCH x-[r]-() delete x,r")
    Set<UnitInstance> deleteUnitByID(@Param(value = "id") String id);

}
