/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTImp;


import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.UnitInstanceRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTInterface.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.RESTInterface.UnitInstanceDAO;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryRule;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Metric;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Duc-Hung Le
 */
public class UnitInstanceDAOImp  implements UnitInstanceDAO {

   
    Logger logger = EliseConfiguration.logger;

    @Autowired
    UnitInstanceRepository repo;

    @Override
    public Set<UnitInstance> getUnitInstanceList() {        
        return repo.listUnitInstance();
    }

    @Override
    public String addUnitInstance(UnitInstance unitInstance) {
        logger.debug("Save UnitInstance: " + unitInstance.getName());

        String uuid = updateComposedIdentification(unitInstance);
        logger.debug("The service is assign UUID: " + uuid);

        UnitInstance existedInstance = this.repo.findByUniqueID(unitInstance.getId());
        
        logger.debug("SHOULD PASS THIS LINE 1");
        // if unit is in data base, merge and save new one 
        // TODO: check Neo4j for better query to update node
        if (existedInstance != null) {
            logger.debug("Merging service ....");
            unitInstance.mergeWith(existedInstance);
            logger.debug("Deleting unit... ");
            this.repo.delete(existedInstance);
//            this.repo.deleteUnitByID(existedInstance.getId());
            logger.debug("Deleting done...");
        }
        
        logger.debug("Start saving. Json: " + unitInstance.toJson());
        
        

        UnitInstance u = repo.save(unitInstance);
        
        logger.debug("Saved ...");
        if (u != null) {
            logger.debug("Saved unit instance:" + u.getId() + ", name: " + u.getName());
            return u.getId();
        } else {
            logger.debug("Fail to save unit instance: " + unitInstance.getId() + ", name: " + unitInstance.getName());
            return null;
        }
    }
    
    @Override
    public Set<UnitInstance> queryUnitInstanceDB(EliseQuery query){
        logger.debug("Query in the local ELISE for instance: " + query.getCategory());        
        logger.debug("Find instance by category: " + query.getCategory());        
        
        Set<UnitInstance> instances = repo.findByCategory(query.getCategory().toString());
        Set<UnitInstance> result = filterInstance(instances, query);
        logger.debug("Found " + result.size() + " of the query: " + query.toJson());
        return result;
    }
    
    
    @Override    
    public Set<UnitInstance> queryUnitInstance(EliseQuery query) {
        logger.debug("Find instance by category: " + query.getCategory());        
        
        Set<UnitInstance> instances = repo.findByCategory(query.getCategory().toString());
        Set<UnitInstance> result = filterInstance(instances, query);
        logger.debug("Found " + result.size() + " of the query: " + query.toJson());
        return result;
    }

    @Override
    public UnitInstance getUnitInstanceByID(String uniqueID) {        
        return repo.findByUniqueID(uniqueID);
    }

    @Override
    public Set<String> getUnitCategory() {
        Set<String> set = new HashSet();
        for (ServiceCategory c : ServiceCategory.values()) {
            set.add(c.toString());
        }
        return set;
    }

    private String updateComposedIdentification(UnitInstance instance) {
        EliseManager collectorService = ((EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider())));
        //LocalIdentification si = LocalIdentification.fromJson(instance.getIdentification());
        
        // unit instance carry the global iden of other elise, should be mix here
        GlobalIdentification gi = GlobalIdentification.fromJson(instance.getIdentification());
        // TODO: may scan all the local iden. Here just consider the first local iden.
        if (gi.getLocalIDs().isEmpty()){
            this.logger.error("The unit instance "+instance.getName()+" contains no identification attribute ! It cannot be add into database.");
            return null;
        }        
        LocalIdentification li = gi.getLocalIDs().get(0);
        logger.debug("Updating identification for instance: " + instance.getName());
        logger.debug("Local ID extracted: " + li.toJson());
        
        GlobalIdentification global = collectorService.updateComposedIdentification(li);
        logger.debug("Global ID after query: " + global.toJson());
        if (global == null) {
            this.logger.error("Cannot get the UUID of the composed-identification. That is impossible to happen !");
            return null;
        }
        instance.setId(global.getUuid());
        instance.setIdentification(global.toJson());
        return global.getUuid();
    }

    
    
    
    
    
    private Set<UnitInstance> filterInstance(Set<UnitInstance> instances, EliseQuery query) {
        Set<UnitInstance> filtered = new HashSet<>();

        logger.debug("Filter " + instances.size() + " of the category: " + query.getCategory().toString());
        int rulefulfill = 0;    // 0: N/A, 1: fulfill, -1: violate
        for (UnitInstance u : instances) {
            logger.debug("Checking instance: " + u.getId() + "/" + u.getName());
            for (Metric value : u.findAllMetricValues()) {
                for (EliseQueryRule rule : query.getRules()) {
                    logger.debug("Comparing unit(" + value.getName() + "=" + value.getValue() + " with the rule " + rule.toString());
                    if (value.getName().equals(rule.getMetric())) {  // if the metric name is match
                        if (rule.isFulfilled(value.getValue())) {    // check if value is fulfill
                            logger.debug("One rule fulfilled !");
                            rulefulfill += 1;                           // add one to the counting of fulfilled value
                        } else {
                            logger.debug("A rule is violated ! BREAK !");
                            rulefulfill -= 1;                           // or reduce it and break as a rule is violated
                            break;
                        }
                    }
                }
                // if all the condition is fulfill, fulfill = rule.size()                
                if (rulefulfill == query.getRules().size()) {           // if rules are fulfilled
                    logger.debug("Fullfill all rules, now checking unit instance if fullfill. Fulfilled count: " + rulefulfill + " with no. of rules: " + query.getRules().size());
                    Set<String> capas = u.findAllCapabilities();       // also check capability
                    if (capas.containsAll(query.getHasCapabilities())) {
                        filtered.add(u);
                    }
                }
            }

        }
        return filtered;
    }
    
    
    
    
    
    
    
}
