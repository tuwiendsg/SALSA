/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;

import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTerm;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author peter
 */
public interface ContractTermRepository extends GraphRepository<ContractTerm> {
    
    @Query("match (n:ContractTerm) return n")
    Set<ContractTerm> listContractTerms();

    @Query("match (n:ContractTerm) where n.name={name} return n")
    ContractTerm findByName(@Param(value = "name") String name);
   
}
