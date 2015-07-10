/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Dependency;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@Service
public class CloudServiceUnitAnalysisEngine {

    public final static String OVERALL_ELASTICITY = "Overall Elasticity";
    public final static String QUALITY_ELASTICITY = "Quality Elasticity";
    public final static String RESOURCE_ELASTICITY = "Resurce Elasticity";
    public final static String SERVICE_UNIT_ASSOCIATION_ELASTICITY = "Service Unit Association Elasticity";
    public final static String COST_ELASTICITY = "Cost Elasticity";

    public AnalysisResult analyzeElasticity(CloudOfferedService unit) {

        AnalysisResult result = new AnalysisResult(unit);

        //can of course be written in a more compact form, but left as this to 
        //enable easy modifiation of the elasticity computation
        int qualityElasticity = 0;
        for (ElasticityCapability capability : unit.getQualityAssociations()) {
            for (Dependency d : capability.getCapabilityDependencies()) {
                qualityElasticity += (d.getDependencyType().equals(ElasticityCapability.Type.OPTIONAL_ASSOCIATION)) ? 1 : -1;
            }
        }

        int resourceElasticity = 0;

        for (ElasticityCapability capability : unit.getResourceAssociations()) {
            for (Dependency d : capability.getCapabilityDependencies()) {
                resourceElasticity += (d.getDependencyType().equals(ElasticityCapability.Type.OPTIONAL_ASSOCIATION)) ? 1 : -1;
            }
        }

        int costElasticity = 0;

        for (ElasticityCapability capability : unit.getCostAssociations()) {
            for (Dependency d : capability.getCapabilityDependencies()) {
                costElasticity += (d.getDependencyType().equals(ElasticityCapability.Type.OPTIONAL_ASSOCIATION)) ? 1 : -1;
            }
        }

        int serviceUnitElasticity = 0;

        for (ElasticityCapability capability : unit.getServiceUnitAssociations()) {
            for (Dependency d : capability.getCapabilityDependencies()) {
                serviceUnitElasticity += (d.getDependencyType().equals(ElasticityCapability.Type.OPTIONAL_ASSOCIATION)) ? 1 : -1;
            }
        }

        int overallElasticity = qualityElasticity + resourceElasticity + costElasticity + serviceUnitElasticity;

        result.addEntry(QUALITY_ELASTICITY, qualityElasticity);
        result.addEntry(RESOURCE_ELASTICITY, resourceElasticity);
        result.addEntry(COST_ELASTICITY, costElasticity);
        result.addEntry(SERVICE_UNIT_ASSOCIATION_ELASTICITY, serviceUnitElasticity);
        result.addEntry(OVERALL_ELASTICITY, overallElasticity);

        return result;

    }

    public List<AnalysisResult> analyzeElasticity(CloudProvider cloudProvider) {

        final List<AnalysisResult> analysisResults = Collections.synchronizedList(new ArrayList<AnalysisResult>());
        List<Thread> threads = new ArrayList<Thread>();

        for (final CloudOfferedService unit : cloudProvider.getCloudOfferedServices()) {
            if (!unit.getCategory().equals("IaaS")) {
                continue;
            }
            Thread t = new Thread() {
                @Override
                public void run() {
                    analysisResults.add(analyzeElasticity(unit));
                }
            };
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CloudServiceUnitAnalysisEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return analysisResults;
    }

//    public void evaluateCost()
//    public void pruneServiceUnits(List<ServiceUnit> serviceUnits, RequirementsElement requirementsElement, EmbeddedGraphDatabase database) {
//
//        Comparator serviceUnitComparator = new Comparator<ServiceUnit>() {
//            public int compare(ServiceUnit o1, ServiceUnit o2) {
//                return o1.getElasticityQuantification().compareTo(o2.getElasticityQuantification());
//            }
//        };
//
//        //first stage is to extract connex components and store them after their elasticity (in one go) 
//        //by extracting the MOST elastic component from each connex component
//        SortedMap<ServiceUnit, Set<ServiceUnit>> connexComponents = new TreeMap<ServiceUnit, Set<ServiceUnit>>(serviceUnitComparator);
//
//        //list containing all units, from which we remove to get the connected components.
//        List<ServiceUnit> toProcess = new ArrayList<ServiceUnit>(serviceUnits);
//        while (!toProcess.isEmpty()) {
//            ServiceUnit processedUnit = toProcess.remove(0);
//
//            //add intial connex component for this element
//            Set<ServiceUnit> list = new TreeSet<ServiceUnit>(serviceUnitComparator);
//            list.add(processedUnit);
//
//            //the light method returns only name, etc, so we will remove them from the toProcess list, and add them to connex
//            List<ServiceUnit> conectedComponentsToThis = ServiceUnitDAO.getConnectedComponentsByElasticityCapabilitiesForNode(processedUnit.getId(), database);
//            while (!conectedComponentsToThis.isEmpty()) {
//                ServiceUnit connectedServiceUnitProcessed = conectedComponentsToThis.remove(0);
//                if (toProcess.contains(connectedServiceUnitProcessed)) {
//                    //add to the connected components the object from the toProcess list. 
//                    list.add(toProcess.remove(toProcess.indexOf(connectedServiceUnitProcessed)));
//                } else {
//                    Configuration.getLogger().log(Level.WARN, "Something weird was returned as " + connectedServiceUnitProcessed + " as it was not found in the initial toProcess list");
//                }
//                //also go deep in the tree and add the connected components
//                conectedComponentsToThis.addAll(ServiceUnitDAO.getConnectedComponentsByElasticityCapabilitiesForNode(connectedServiceUnitProcessed.getId(), database));
//            }
//
//            //as the sorted set is sorted after the elasticity, here we have at position 0 in the set the most elastic service unit, or
//            //in other workds, the component on which other service units depend the most
//            connexComponents.put(list.iterator().next(), list);
//        }
//
//
//        //now we have a sorted map after the elasticity of the root
//        //from here we go trough each connex component, get the first which matches some requirements, and apply set cover on it. 
//        //TODO: continue from here
//        
//
//    }
//    public int getNrOfRequirementsMatched(Requirements requirements, )
    public class AnalysisResult {
        //use to keep the keys to avoid getting them from the map.
        //if proven useless

        private CloudOfferedService unit;
        private List<String> resultFields;
        private Map<String, Object> analysis;

        {
            resultFields = new ArrayList<String>();
            analysis = new HashMap<String, Object>();
        }

        public AnalysisResult(CloudOfferedService unit) {
            this.unit = unit;
        }

        public void addEntry(String key, Object value) {
            if (!resultFields.contains(key)) {
                resultFields.add(key);
            }
            analysis.put(key, value);
        }

        public Map<String, Object> getAnalysis() {
            return analysis;
        }

        public List<String> getResultFields() {
            return resultFields;
        }

        public CloudOfferedService getUnit() {
            return unit;
        }

        public Object getValue(String key) {
            return analysis.get(key);
        }
    }
}
