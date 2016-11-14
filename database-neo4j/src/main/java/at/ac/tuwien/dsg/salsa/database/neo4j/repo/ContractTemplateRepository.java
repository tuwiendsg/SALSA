/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.neo4j.repo;


import at.ac.tuwien.dsg.salsa.model.extra.contract.ContractTemplate;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author peter
 */
public interface ContractTemplateRepository extends GraphRepository<ContractTemplate> {
    
    @Query("match (n:ContractTemplate) return n")
    Set<ContractTemplate> listContractTemplates();

    @Query("match (n:ContractTemplate) where n.name={name} return n")
    ContractTemplate findByName(@Param(value = "name") String name);
   
}
