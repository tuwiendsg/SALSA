/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;

import at.ac.tuwien.dsg.cloud.elise.model.relationships.HostOnRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipGraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 *
 * @author hungld
 */
public interface HostOnInstanceRelationshipRepository extends GraphRepository<HostOnRelationshipInstance> {
    
}
