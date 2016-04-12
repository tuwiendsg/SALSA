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

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ArtifactRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ConnectToInstanceRelationshipRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.HostOnInstanceRelationshipRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ProviderRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ServiceTemplateRepository;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.UnitInstanceRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQueryRule;
import at.ac.tuwien.dsg.cloud.elise.model.relationships.ConnectToRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.relationships.HostOnRelationshipInstance;

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Artifact;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Provider;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;

/**
 *
 * @author Duc-Hung Le
 */
@Service
@Configurable
public class EliseRepositoryImpl implements EliseRepository {

    Logger logger = EliseConfiguration.logger;

    @Autowired
    UnitInstanceRepository repo;

    @Autowired
    ConnectToInstanceRelationshipRepository connectToRepo;

    @Autowired
    HostOnInstanceRelationshipRepository hostOnRepo;

    @Autowired
    ProviderRepository pdrepo;

    @Autowired
    ArtifactRepository artifactRepo;

    @Autowired
    ServiceTemplateRepository serviceTemplateRepo;

    @Override
    public Set<UnitInstance> readAllUnitInstances(String name, String category, String state, String hostedOnID) {
        Set<UnitInstance> theSet = repo.listUnitInstance();
        if (theSet != null) {
            logger.debug("Getting list of the instance, size: {}", theSet.size());
            for (UnitInstance instance : theSet) {
                if ((name != null && !instance.getName().equals(name))
                        || (category != null && !instance.getCategory().toString().equals(category))
                        || (state != null && !instance.getState().toString().toLowerCase().equals(state))
                        || (hostedOnID != null && instance.getHostedOn() != null && !instance.getHostedOn().getUuid().equals(hostedOnID))) {
                    theSet.remove(instance);
                }
            }
            return theSet;
        } else {
            logger.error("Getting list of the instance: FAILED.");
            return null;
        }
    }

    @Override
    public UnitInstance readUnitInstance(String uniqueID) {
        UnitInstance instance = repo.findByUniqueID(uniqueID);
        if (instance != null) {
            logger.debug("Get unit instance in elise DB, found name={},category={}", instance.getName(), instance.getCategory());
        } else {
            logger.error("Cannot get instance with id: {}", uniqueID);
        }
        return instance;
    }

    @Override
    public UnitInstance saveUnitInstance(UnitInstance unitInstance) {
        logger.debug("Save UnitInstance: " + unitInstance.getName());

        UnitInstance existedInstance = null;
        List<String> uuids = updateComposedIdentification(unitInstance);
        StringBuilder result = new StringBuilder();
        for (String uuid : uuids) {
            result.append(uuid).append(" ");
            //String uuid = updateComposedIdentification(unitInstance);
            logger.debug("Found an instance can be merged with UUID: " + uuid);
            existedInstance = this.repo.findByUniqueID(uuid);

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
                logger.debug("Saved unit instance:" + u.getUuid() + ", name: " + u.getName());

            } else {
                logger.debug("Fail to save unit instance: " + unitInstance.getUuid() + ", name: " + unitInstance.getName());

            }
        }
        return existedInstance;
    }

    @Override
    public Set<UnitInstance> readUnitInstanceByExtension(List<ExtensibleModel> extra) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<UnitInstance> query(EliseQuery query) {
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
        GlobalIdentification gi = instance.getIdentification();// GlobalIdentification.fromJson(instance.getIdentification());

        // TODO: may scan all the local iden. Here just consider the first local iden.
        if (gi == null || gi.getLocalIDs() == null || gi.getLocalIDs().isEmpty()) {
            this.logger.error("The unit instance " + instance.getName() + " contains no identification attribute ! It cannot be add into database.");
            return null;
        }
        LocalIdentification li = gi.getLocalIDs().get(0);
        logger.debug("Updating identification for instance: " + instance.getName());
        logger.debug("Local ID extracted: " + li.toJson());

        // if there is no global ID exist, the instance.getId() will be the new ID
        List<GlobalIdentification> globals = eliseManager.updateComposedIdentification(li, instance.getUuid());
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
            logger.debug("Checking instance: " + u.getUuid() + "/" + u.getName());

            Set<ExtensibleModel> extras = u.getExtra();
            for (ExtensibleModel extra : extras) {
                //for (Map.Entry<String, Object> entry : extra.entrySet()) {
//            for (Metric value : u.findAllMetricValues()) {
                for (EliseQueryRule rule : query.getRules()) {
                    logger.debug("Comparing unit(" + extra.getClazz().getSimpleName() + "=" + rule.getMetric() + " with the rule " + rule.toString());

                    if (extra.getClazz().getSimpleName().equals(rule.getMetric())) {  // if the metric name is match
                        if (rule.isFulfilled(rule.getMetric())) {    // check if value is fulfill
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
    public void deleteUnitInstance(String uniqueID) {
        UnitInstance existedInstance = this.repo.findByUniqueID(uniqueID);
        if (existedInstance != null) {
            logger.debug("Deleting unit instance ID {}", uniqueID);
            repo.delete(existedInstance);
        } else {
            logger.debug("Cannot delete the instance unit from GraphDB with ID: {}", uniqueID);
        }
    }

    @Override
    public void saveRelationshipHostOn(HostOnRelationshipInstance hostOnRela) {
        logger.debug("Saving relationship between: " + hostOnRela.getFrom().getUuid() + " and " + hostOnRela.getTo().getUuid() + ". Json: " + hostOnRela.toJson());
//        this.hostOnRepo.createRelationshipBetween(hostOnRela.getFrom(), hostOnRela.getTo(), HostOnRelationshipInstance.class, "HostOn");
        if (this.hostOnRepo == null) {
            logger.error("hostOnRepo is null !!");
            return;
        }
        this.hostOnRepo.save(hostOnRela);
    }

    @Override
    public void saveRelationshipConnectTo(ConnectToRelationshipInstance connectToRela) {
        logger.debug("Saving relationship between: " + connectToRela.getFrom().getUuid() + " and " + connectToRela.getTo().getUuid() + ". Json: " + connectToRela.toJson());
//        this.connectToRepo.createRelationshipBetween(connectToRela.getFrom(), connectToRela.getTo(), ConnectToRelationshipInstance.class, "ConnectTo");
        if (this.connectToRepo == null) {
            logger.error("connectToRepo is null !!");
            return;
        }
        this.connectToRepo.save(connectToRela);
    }

    @Override
    public Provider readProvider(String uniqueID) {
        return pdrepo.findByUniqueID(uniqueID);
    }

    @Override
    public Set<Provider> readAllProviders() {
        return pdrepo.listProviders();
    }

    @Override
    public String saveProvider(Provider provider) {
        if (pdrepo == null) {
            logger.error("Cannot load ProviderRepository !");
            return null;
        }

        logger.debug("Prepare to add provider: ID=" + provider.getUuid());
        if (provider.getOffering() != null) {
            logger.debug("This provider has " + provider.getOffering().size() + " OSU(s)");
        }
//        for (GenericServiceUnit u : provider.getOffering()) {
//            logger.debug("Prepare to add offering: " + u.getId() + " - " + u.getCategory() );
//            offerServiceDAO.addOfferServiceUnitForProvider(u, provider.getId());
//        }
        Provider r = pdrepo.save(provider);
        return "Saved the provider to graph with id: " + r.getUuid();
    }

    @Override
    public void deleteProvider(String uniqueID) {
        this.pdrepo.deleteProviderCompletelyByID(uniqueID);
    }

    @Override
    public ServiceTemplate readServiceTemplate(String uniqueID) {
        return serviceTemplateRepo.findByUniqueID(uniqueID);
    }

    @Override
    public Set<ServiceTemplate> readAllServiceTemplates() {
        return serviceTemplateRepo.listServiceTemplate();
    }

    @Override
    public ServiceTemplate saveServiceTemplate(ServiceTemplate serviceTemplate) {
        ServiceTemplate existed = serviceTemplateRepo.findByUniqueID(serviceTemplate.getUuid());
        if (existed != null) {
            serviceTemplate.setGraphID(existed.getGraphID());
        }
        return serviceTemplateRepo.save(serviceTemplate);
    }

    @Override
    public void deleteServiceTemplate(String uniqueID) {
        ServiceTemplate s = serviceTemplateRepo.findByUniqueID(uniqueID);
        if (s != null) {
            serviceTemplateRepo.delete(s);
        }
    }

    /**
     * MANAGE ARTIFACT *
     */
    @Override
    public Set<Artifact> readArtifact(String name, String version, String type) {
        Set<Artifact> arts = artifactRepo.findByName(name);
        for (Artifact a : arts) {
            if ((version != null && !version.equals(a.getVersion()))
                    || (type != null && !type.equals(a.getType().toString()))) {
                arts.remove(a);
            }
        }
        return arts;
    }

    @Override
    public Artifact saveArtifact(Artifact artifact) {
        System.out.println("Saving artifact...: " + artifact.writeToJson());
        logger.debug("Saving artifact...: " + artifact.writeToJson());
        return artifactRepo.save(artifact);
    }

    @Override
    public void deleteArtifact(Artifact artifact) {
        artifactRepo.delete(artifact);
    }

    public String health() {
        return "Service is alive !";
    }

}
