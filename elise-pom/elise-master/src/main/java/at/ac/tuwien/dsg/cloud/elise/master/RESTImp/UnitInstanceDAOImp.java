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
package at.ac.tuwien.dsg.cloud.elise.master.RESTImp;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.UnitInstanceRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.UnitInstanceDAO;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryRule;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Metric;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntities;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Duc-Hung Le
 */
public class UnitInstanceDAOImp implements UnitInstanceDAO {

    Logger logger = EliseConfiguration.logger;

    @Autowired
    UnitInstanceRepository repo;

    @Override
    public Set<UnitInstance> getUnitInstanceList() {
        Set<UnitInstance> theSet = repo.listUnitInstance();
        if (theSet != null) {
            logger.debug("Getting list of the instance, size: {}", theSet.size());
        } else {
            logger.error("Getting list of the instance: FAILED.");
        }
        return theSet;
    }

    @Override
    public UnitInstance getUnitInstanceByID(String uniqueID) {
        UnitInstance instance = repo.findByUniqueID(uniqueID);
        if (instance != null) {
            logger.debug("Get unit instance in elise DB, found name={},category={}", instance.getName(), instance.getCategory());
        } else {
            logger.error("Cannot get instance with id: {}", uniqueID);
        }
        return instance;
    }

    @Override
    public UnitInstance getUnitInstanceByIDFullStack(String uniqueID) {
        logger.debug("Getting full stack information of instance id: {}", uniqueID);
        UnitInstance theInstance = repo.findByUniqueID(uniqueID);
        if (theInstance == null) {
            return null;
        }
        Set<UnitInstance> hostedInstances = repo.findByHostOn(uniqueID);
        DomainEntities full = new DomainEntities();
        for (UnitInstance i : hostedInstances) {
            logger.debug("Aggregrate hosted instance information: name={}, category={}", i.getName(), i.getCategory());
            DomainEntity entity = DomainEntity.fromJson(i.getDomainInfo());
            if (entity != null) {
                logger.debug("Found and adding domainEntity of unit instance name={}, category={}", i.getName(), i.getCategory());
                full.hasDomainEntity(entity);
            } else {
                logger.debug("Do not found domainEntity of unit instance name={}, category={}", i.getName(), i.getCategory());
            }
        }
        theInstance.setDomainInfo(full.toJson());
        return theInstance;
    }

    @Override
    public UnitInstance getUnitInstanceFirstByName(String unitName) {
        logger.debug("Getting unit instance by name: {}", unitName);
        return repo.findByName(unitName);
    }

    @Override
    public UnitInstance getUnitInstanceFirstByNameFullStack(String name) {
        logger.debug("Getting unit instance full stack by name: {}", name);
        UnitInstance theInstance = repo.findByName(name);
        Set<UnitInstance> instances = repo.findByHostOn(theInstance.getId());
        DomainEntities full = new DomainEntities();
        for (UnitInstance i : instances) {
            logger.debug("Found and adding domainEntity of instance name={}, category={}", i.getName(), i.getCategory());
            DomainEntity entity = DomainEntity.fromJson(i.getDomainInfo());
            full.hasDomainEntity(entity);
        }
        theInstance.setDomainInfo(full.toJson());
        return theInstance;
    }

    @Override
    public String addUnitInstance(UnitInstance unitInstance) {
        logger.debug("Save UnitInstance: " + unitInstance.getName());

        List<String> uuids = updateComposedIdentification(unitInstance);
        StringBuilder result = new StringBuilder();
        for (String uuid : uuids) {
            result.append(uuid).append(" ");
            //String uuid = updateComposedIdentification(unitInstance);
            logger.debug("Found an instance can be merged with UUID: " + uuid);
            UnitInstance existedInstance = this.repo.findByUniqueID(uuid);

            logger.debug("SHOULD PASS THIS LINE 1");
            // if unit is in data base, merge and save new one 
            // TODO: check Neo4j for better query to update node
            if (existedInstance != null) {
                //logger.debug("Deleting instance unit... ");
                //this.repo.delete(existedInstance);
                logger.debug("Deleting done..., now merging instance ...");
                existedInstance.mergeWith(unitInstance);
//            this.repo.deleteUnitByID(existedInstance.getId());
            } else {
                logger.debug("Found no existing instance, this saving instance will be the new one !");
                existedInstance = unitInstance;
            }

            logger.debug("Start saving. Json: " + existedInstance.toJson());

            UnitInstance u = this.repo.save(existedInstance);

            logger.debug("Saved ...");
            if (u != null) {
                logger.debug("Saved unit instance:" + u.getId() + ", name: " + u.getName());

            } else {
                logger.debug("Fail to save unit instance: " + unitInstance.getId() + ", name: " + unitInstance.getName());

            }
        }
        return result.toString().trim();
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
    public Set<String> getUnitCategory() {
        Set<String> set = new HashSet();
        for (ServiceCategory c : ServiceCategory.values()) {
            set.add(c.toString());
        }
        return set;
    }

    private List<String> updateComposedIdentification(UnitInstance instance) {
        EliseManager eliseManager = ((EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider())));
        //LocalIdentification si = LocalIdentification.fromJson(instance.getIdentification());

        // unit instance carry the global iden of other elise, should be mix here
        GlobalIdentification gi = GlobalIdentification.fromJson(instance.getIdentification());
        // TODO: may scan all the local iden. Here just consider the first local iden.
        if (gi.getLocalIDs().isEmpty()) {
            this.logger.error("The unit instance " + instance.getName() + " contains no identification attribute ! It cannot be add into database.");
            return null;
        }
        LocalIdentification li = gi.getLocalIDs().get(0);
        logger.debug("Updating identification for instance: " + instance.getName());
        logger.debug("Local ID extracted: " + li.toJson());

        // if there is no global ID exist, the instance.getId() will be the new ID
        List<GlobalIdentification> globals = eliseManager.updateComposedIdentification(li, instance.getId());
        if (globals.isEmpty()) {
            this.logger.error("Cannot get the UUID of the composed-identification. That is impossible to happen !");
            return null;
        }
        logger.debug("Global ID after query: {} IDs", globals.size());

//        instance.setId(global.getUuid());
//        instance.setIdentification(global.toJson());
        List<String> listOfGlobalID = new ArrayList<>();
        for (GlobalIdentification gl : globals) {
            listOfGlobalID.add(gl.getUuid());
        }
        return listOfGlobalID;
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

    @Override
    public void deleteUnitInstanceByID(String uniqueID) {
        UnitInstance existedInstance = this.repo.findByUniqueID(uniqueID);
        if (existedInstance != null) {
            logger.debug("Deleting unit instance ID {}", uniqueID);
            repo.delete(existedInstance);
        } else {
            logger.debug("Cannot delete the instance unit from GraphDB with ID: {}", uniqueID);
        }
    }

}
