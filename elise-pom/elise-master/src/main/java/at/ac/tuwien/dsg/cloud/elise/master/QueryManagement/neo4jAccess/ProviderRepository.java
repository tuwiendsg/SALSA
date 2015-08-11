/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider.Provider;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Duc-Hung Le
 */
 
public interface ProviderRepository extends GraphRepository<Provider> {

    @Query("match (n:Provider) return n")
    Set<Provider> listProviders();

    @Query("match (n:Provider) where n.uniqueID={uniqueID} return n")
    Provider findByUniqueID(@Param(value = "uniqueID") String uniqueID);

    @Query("match (n:Provider) where n.name={name} return n")
    Provider findByName(@Param(value = "name") String name);

    @Query("match (n:Provider)-[*]->x where n.id={providerId} WITH x MATCH x-[r]-() delete x,r")
    public void deleteProviderCompletelyByID(@Param("providerId") String paramString);

}
