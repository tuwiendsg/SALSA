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
package at.ac.tuwien.dsg.quelle.elasticityQuantification.engines;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostElement;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Unit;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.Strategy;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.StrategyCategory;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
/**
 * All comparators return -1 * something, to put first the one with maximum
 * compared value
 *
 * @author daniel-tuwien
 */
@Component
public class ServiceUnitComparators {

    //@Autowired
    private CloudServiceElasticityAnalysisEngine cloudServiceElasticityAnalysisEngine = new CloudServiceElasticityAnalysisEngine();

    //hold comparator instances to be reused
    private final Map<StrategyCategory, Comparator> comparators = new EnumMap<StrategyCategory, Comparator>(StrategyCategory.class);

    {
        comparators.put(StrategyCategory.OVERALL_ELASTICITY, new OverallElasticityComparator());
        comparators.put(StrategyCategory.QUALITY_ELASTICITY, new QualityElasticityComparator());
        comparators.put(StrategyCategory.COST_ELASTICITY, new CostElasticityComparator());
        comparators.put(StrategyCategory.RESOURCE_ELASTICITY, new ResourceElasticityComparator());
        comparators.put(StrategyCategory.SERVICE_UNITS_ASSOCIATIONS_ELASTICITY, new ServiceUnitAssociationElasticityComparator());
        comparators.put(StrategyCategory.OVERALL_REQUIREMENTS, new OverallRequirementsComparator());
        comparators.put(StrategyCategory.QUALITY_REQUIREMENTS, new QualityRequirementsComparator());
        comparators.put(StrategyCategory.COST_REQUIREMENTS, new CostRequirementsComparator());
        comparators.put(StrategyCategory.RESOURCE_REQUIREMENTS, new ResourceRequirementsComparator());
        comparators.put(StrategyCategory.QUALITY_PROPERTIES, new QualityPropertiesComparator());

        comparators.put(StrategyCategory.COST_PROPERTIES, new CostPropertiesComparator());
        comparators.put(StrategyCategory.MINIMUM_COST, new MinimumCostPropertiesComparator());

        comparators.put(StrategyCategory.MINIMUM_RESOURCES, new ResourcePropertiesComparator());

    }

    public List<Comparator<ServiceUnitOptions>> getStrategiesComparators(List<Strategy> strategies) {
        List<Comparator<ServiceUnitOptions>> selectedComparators = new ArrayList<Comparator<ServiceUnitOptions>>();
        for (Strategy strategy : strategies) {
            StrategyCategory category = strategy.getStrategyCategory();
            if (comparators.containsKey(category)) {
                selectedComparators.add(comparators.get(category));
            } else {
                Logger.getLogger(ServiceUnitComparators.class.getName()).log(Level.SEVERE, "StrategyCategory " + category + " does not have comparator");
            }
        }
        return selectedComparators;
    }

    public Comparator<ServiceUnitOptions> getStrategiesComparator(StrategyCategory StrategyCategory) {
        return comparators.get(StrategyCategory);
    }

    private class OverallRequirementsComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            return ((Integer) o1.getOverallUnMatched().size()).compareTo(o2.getOverallUnMatched().size());
        }
    }

    private class QualityRequirementsComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            Integer firstQualityMatched = 0;
            Integer secondQualityMatched = 0;

            for (ElasticityCapability q : o1.getOptionalQualities().keySet()) {
                firstQualityMatched += o1.getOptionalQualities().get(q).iterator().next().getMatchedRequirements().size();
            }

            for (ElasticityCapability q : o2.getOptionalQualities().keySet()) {
                secondQualityMatched += o2.getOptionalQualities().get(q).iterator().next().getMatchedRequirements().size();
            }

            return -1 * (firstQualityMatched.compareTo(secondQualityMatched));

        }
    }

    private class ResourceRequirementsComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            Integer firstQualityMatched = 0;
            Integer secondQualityMatched = 0;

            for (ElasticityCapability r : o1.getOptionalResources().keySet()) {
                firstQualityMatched += o1.getOptionalResources().get(r).iterator().next().getMatchedRequirements().size();
            }

            for (ElasticityCapability r : o2.getOptionalResources().keySet()) {
                secondQualityMatched += o2.getOptionalResources().get(r).iterator().next().getMatchedRequirements().size();
            }

            return -1 * (firstQualityMatched.compareTo(secondQualityMatched));

        }
    }

    private class OverallElasticityComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            return -1 * ((Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.OVERALL_ELASTICITY)).compareTo(
                    (Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.OVERALL_ELASTICITY));
        }
    }

    private class QualityElasticityComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {            
//            System.out.println("o1.serviceunit: "+ o1.getServiceUnit());
//            System.out.println("cloudServiceElasticityAnalysisEngine: " + cloudServiceElasticityAnalysisEngine);            
//            System.out.println("analyzeElasticity: " + cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()));
            return -1 * ((Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.QUALITY_ELASTICITY)).compareTo(
                    (Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.QUALITY_ELASTICITY));
        }
    }

    private class CostElasticityComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            return -1 * ((Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.COST_ELASTICITY)).compareTo(
                    (Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.COST_ELASTICITY));
        }
    }

    private class ResourceElasticityComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            return -1 * ((Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.RESOURCE_ELASTICITY)).compareTo(
                    (Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.RESOURCE_ELASTICITY));
        }
    }

    private class ServiceUnitAssociationElasticityComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            return -1 * ((Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o1.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.SERVICE_UNIT_ASSOCIATION_ELASTICITY)).compareTo(
                    (Double) cloudServiceElasticityAnalysisEngine.analyzeElasticity(o2.getServiceUnit()).getValue(cloudServiceElasticityAnalysisEngine.SERVICE_UNIT_ASSOCIATION_ELASTICITY));
        }
    }

    private class QualityPropertiesComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {

            //for each encountered Quality q, if o1 > o2 || o2 does not have q, put 1, else -1, else 0. 
            int score = 0;

            List<Quality> o1Qualities = new ArrayList<Quality>();
            List<Quality> o2Qualities = new ArrayList<Quality>();

            //add quality of service unit
            o1Qualities.addAll(o1.getServiceUnit().getQualityProperties());
            //add best quality from each optional quality
            for (ElasticityCapability key : o1.getOptionalQualities().keySet()) {
                //add first, which has best quality, as this is how IT is added to the map. 
                //the quality set in the  map is a sorted TreeSet
                o1Qualities.add(o1.getOptionalQualities().get(key).iterator().next().getConcreteConfiguration());
            }

            o2Qualities.addAll(o2.getServiceUnit().getQualityProperties());
            //add best quality from each optional quality
            for (ElasticityCapability key : o2.getOptionalQualities().keySet()) {
                //add first, which has best quality, as this is how IT is added to the map. 
                //the quality set in the  map is a sorted TreeSet
                o1Qualities.add(o2.getOptionalQualities().get(key).iterator().next().getConcreteConfiguration());
            }

            //go trough each quality in o1 and check if it is present in o2
            while (!o1Qualities.isEmpty()) {
                Quality q1 = o1Qualities.remove(0);

                //need to check this equality if it returns ok
                if (o2Qualities.contains(q1)) {
                    Quality q2 = o2Qualities.get(o2Qualities.indexOf(q1));

                    Map<Metric, MetricValue> q1Properties = q1.getProperties();
                    Map<Metric, MetricValue> q2Properties = q2.getProperties();
                    int evalResult = 0;
                    //in theory if they are equal, q1 and q2 should have same metrics
                    for (Metric q1Metric : q1Properties.keySet()) {
                        if (q1Properties.containsKey(q1Metric)) {
                            evalResult += q1Properties.get(q1Metric).compareTo(q2Properties.get(q1Metric));
                        } else {
                            evalResult += 1;
                        }
                    }
                    score += evalResult;
                } else {
                    score += 1;
                }
            }

            //go trough remaining qualities offe3red by o2
            while (!o2Qualities.isEmpty()) {
                Quality q2 = o2Qualities.remove(0);

                //o2 is logically smaller than o2 is O2 has more quality attributed
                score += -1;
            }

            return score;
        }
    }

    private class ResourcePropertiesComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {

            //for each encountered Resource q, if o1 > o2 || o2 does not have q, put 1, else -1, else 0. 
            int score = 0;

            List<Resource> r1Resources = new ArrayList<Resource>();
            List<Resource> r2Resources = new ArrayList<Resource>();

            //add quality of service unit
            r1Resources.addAll(o1.getServiceUnit().getResourceProperties());
            //add best quality from each optional quality
            for (ElasticityCapability key : o1.getOptionalResources().keySet()) {
                //add first, which has best quality, as this is how IT is added to the map. 
                //the quality set in the  map is a sorted TreeSet
                r1Resources.add(o1.getOptionalResources().get(key).iterator().next().getConcreteConfiguration());
            }

            r2Resources.addAll(o2.getServiceUnit().getResourceProperties());
            //add best quality from each optional quality
            for (ElasticityCapability key : o2.getOptionalResources().keySet()) {
                //add first, which has best quality, as this is how IT is added to the map. 
                //the quality set in the  map is a sorted TreeSet
                r1Resources.add(o2.getOptionalResources().get(key).iterator().next().getConcreteConfiguration());
            }

            //go trough each resource in o1 and check if it is present in o2
            while (!r1Resources.isEmpty()) {
                Resource r1 = r1Resources.remove(0);
                boolean hasResource = false;
                for (Resource r2 : r2Resources) {
                    if (r2.getName().equals(r1.getName())) {
                        r2Resources.remove(r2);

                        Map<Metric, MetricValue> q1Properties = r1.getProperties();
                        Map<Metric, MetricValue> q2Properties = r2.getProperties();
                        int evalResult = 0;
                        //in theory if they are equal, q1 and q2 should have same metrics
                        for (Metric q1Metric : q1Properties.keySet()) {
                            if (q1Properties.containsKey(q1Metric)) {
                                evalResult += q1Properties.get(q1Metric).compareTo(q2Properties.get(q1Metric));
                            } else {
                                evalResult += 1;
                            }
                        }
                        score += evalResult;
                        hasResource = true;
                        break;
                    }
                }

                if (!hasResource) {
                    score += 1;
                }
            }

            //go trough remaining Resource offe3red by o2
            while (!r2Resources.isEmpty()) {
                Resource r2 = r2Resources.remove(0);

                //o2 is logically smaller than o1 is O2 has more resource attributed
                score += -1;
            }

            return score;
        }
    }

    private class CostPropertiesComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
//            List<ElasticityCapability> costCapabilities_1 = o1.getServiceUnit().getOptionalCostAssociations();
//            List<ElasticityCapability> costCapabilities_2 = o2.getServiceUnit().getOptionalCostAssociations();
//            
//            
//            List<CostFunction> cost_1 = new ArrayList<CostFunction>();
//            List<CostFunction> cost_2 = new ArrayList<CostFunction>();
//            
//            for(ElasticityCapability capability: costCapabilities_1){
//                for(o1.getOptionallyConnectedServiceUnitsReport()
//            }
//            
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class MinimumCostPropertiesComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {

            //sum up standard cost function
            class CostEvaluator {

                public Map<Metric, CostElement> sumCost(ServiceUnitOptions options) {
                    Map<Metric, CostElement> elements = new HashMap<>();
//
//                    //check how much of the optional costs have been used
                    List<ElasticityCapability> costElasticity = options.getServiceUnit().getCostAssociations();
//
                    //contains quality, resources and other service units
                    List<Unit> chosenOptions = new ArrayList<>();

                    chosenOptions.addAll(options.getSolutionQualities());
                    chosenOptions.addAll(options.getSolutionResources());
                    chosenOptions.addAll(options.getSolutionServiceUnits());
//
//                    //currently to compare,  we use the "cheapest" cost
                    //costs without target means optional costs for THIS entity, not if used in conjunction with another. That cost is separate.
                    List<CostFunction> optionalCostsNotAsociatedToTarget = new ArrayList<>();
//
//                    //for each chosen entity, if we have a cost function to it, get all cost elements
                    for (ElasticityCapability capability : costElasticity) {
                        for (ElasticityCapability.Dependency dep : capability.getCapabilityDependencies()) {
                            CostFunction f = (CostFunction) dep.getTarget();
                            if (f.getAppliedIfServiceInstanceUses().isEmpty()) {
                                optionalCostsNotAsociatedToTarget.add(f);
                            } else {
//                                //else search for target
                                for (Unit entity : chosenOptions) {
                                    if (f.getAppliedIfServiceInstanceUses().contains(entity)) {
                                        for (CostElement element : f.getCostElements()) {
                                            elements.put(element.getCostMetric(), element);
                                        }
                                    }
                                }
                            }

                        }
                    }

                    //sort by cheapest cost
                    Collections.sort(optionalCostsNotAsociatedToTarget, new Comparator<CostFunction>() {

                        @Override
                        public int compare(CostFunction o1, CostFunction o2) {
                            Map<Metric, CostElement> costElements_o1 = new HashMap<>();
                            Map<Metric, CostElement> costElements_o2 = new HashMap<>();

                            for (CostElement element : o1.getCostElements()) {
                                costElements_o1.put(element.getCostMetric(), element);
                            }

                            for (CostElement element : o2.getCostElements()) {
                                costElements_o2.put(element.getCostMetric(), element);
                            }

                            int comparrisonResult = 0;

                            for (Metric metric : costElements_o1.keySet()) {
                                if (costElements_o2.containsKey(metric)) {
                                    comparrisonResult += costElements_o1.get(metric).compareTo(costElements_o2.get(metric));
                                }
                            }

                            return comparrisonResult;

                        }

                    });

                    if (!optionalCostsNotAsociatedToTarget.isEmpty()) {
                        //add cheapest cost to cost elements
                        //done such that the options have selected cost. this is not good.
                        //the "if" is a sort of hack to prevent multiple additions of same "cheapest" cost 
                        if (!options.getServiceUnit().getCostFunctions().contains(optionalCostsNotAsociatedToTarget.get(0))) {
                            options.getServiceUnit().getCostFunctions().add(optionalCostsNotAsociatedToTarget.get(0));
                        }

                        //add cheapest cost to cost elements
                        for (CostElement element : optionalCostsNotAsociatedToTarget.get(0).getCostElements()) {
                            elements.put(element.getCostMetric(), element);
                        }
                    }

                    //get all cost options which do not have target
                    //add standard cost elements
                    for (CostFunction f : options.getServiceUnit().getCostFunctions()) {
                        for (CostElement element : f.getCostElements()) {
                            elements.put(element.getCostMetric(), element);
                        }
                    }

                    return elements;
                }
            }

            Map<Metric, CostElement> costElements_o1 = new CostEvaluator().sumCost(o1);
            Map<Metric, CostElement> costElements_o2 = new CostEvaluator().sumCost(o2);

            int comparrisonResult = 0;

            for (Metric metric : costElements_o1.keySet()) {
                if (costElements_o2.containsKey(metric)) {
                    comparrisonResult += costElements_o1.get(metric).compareTo(costElements_o2.get(metric));
                }
            }

            return comparrisonResult;

        }
    }

    private class CostRequirementsComparator implements Comparator<ServiceUnitOptions> {

        public int compare(ServiceUnitOptions o1, ServiceUnitOptions o2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
