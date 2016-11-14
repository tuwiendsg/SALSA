/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.repo;

import at.ac.tuwien.dsg.salsa.model.extra.contract.Script;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author peter
 */
public interface ScriptRepository extends GraphRepository<Script> {
    
    @Query("match (n:Script) return n")
    Set<Script> listScripts();

    @Query("match (n:Script) where n.name={name} return n")
    Script findByName(@Param(value = "name") String name);
    
}
