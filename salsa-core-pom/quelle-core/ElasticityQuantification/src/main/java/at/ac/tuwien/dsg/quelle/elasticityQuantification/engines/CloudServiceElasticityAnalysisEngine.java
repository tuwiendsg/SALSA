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
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Phase;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Type;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Volatility;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.evaluationFunctions.ElasticityCapabilityQuantificationFunction;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.evaluationFunctions.ElasticityDependencyEvalFunction;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.evaluationFunctions.ElasticityPhaseEvalFunction;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.evaluationFunctions.ElasticityVolatilityEvalFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@Component
public class CloudServiceElasticityAnalysisEngine extends CloudServiceUnitAnalysisEngine {

    public AnalysisResult analyzeElasticity(CloudOfferedService unit) {

        //elastiicy eval functions
        ElasticityVolatilityEvalFunction evef = new ElasticityVolatilityEvalFunction() {
            @Override
            public Double eval(Volatility volatility) {
                return (volatility == null) ? 1 : volatility.getVolatility();
            }
        };

        ElasticityPhaseEvalFunction epef = new ElasticityPhaseEvalFunction() {
            @Override
            public Double eval(String phase) {
                Double value = 0.0;
                switch (phase) {
                    case Phase.INSTANTIATION_TIME:
                        value = 1.0;
                        break;
                    case Phase.RUN_TIME:
                        value = 2.0;
                        break;
                    case Phase.BOTH:
                        value = 3.0;
                        break;
                    default:
                        System.err.println("Phase \"" + phase + "\" not recognized");
                }
                return value;
            }

        };
        ElasticityDependencyEvalFunction edef = new ElasticityDependencyEvalFunction() {
            @Override
            public Double eval(String type) {
                Double value = 0.0;
                switch (type) {
                    case Type.MANDATORY_ASSOCIATION:
                        value = -1.0;
                        break;
                    case Type.OPTIONAL_ASSOCIATION:
                        value = 1.0;
                        break;
                    default:
                        System.err.println("Dependency type \"" + type + "\" not recognized");
                }
                return value;
            }
        };

        AnalysisResult result = new AnalysisResult(unit);

        //can of course be written in a more compact form, but left as this to 
        //enable easy modifiation of the elasticity computation
        Double overallElasticity = 0.0;

        //quantify overall elasticity
        {
            Map< Class, Double> weights = new HashMap<>();

            weights.put(CostFunction.class, 1.0);
            weights.put(Quality.class, 1.0);
            weights.put(Resource.class, 1.0);
            weights.put(CloudOfferedService.class, 1.0);

            overallElasticity = ElasticityCapabilityQuantificationFunction.eval(unit, weights, evef, epef, edef);

        }

        Double qualityElasticity = 0.0;
        //quantify quality elasticity
        {

            qualityElasticity = ElasticityCapabilityQuantificationFunction.eval(unit, Quality.class, evef, epef, edef);

        }

        Double resourceElasticity = 0.0;
        //quantify quality elasticity
        {

            resourceElasticity = ElasticityCapabilityQuantificationFunction.eval(unit, Resource.class, evef, epef, edef);

        }

        Double costElasticity = 0.0;
        //quantify quality elasticity
        {
            costElasticity = ElasticityCapabilityQuantificationFunction.eval(unit, CostFunction.class, evef, epef, edef);
        }

        Double serviceUnitElasticity = 0.0;
        //quantify quality elasticity
        {

            serviceUnitElasticity = ElasticityCapabilityQuantificationFunction.eval(unit, CloudOfferedService.class, evef, epef, edef);

        }

//        int overallElasticity = qualityElasticity + resourceElasticity + costElasticity + serviceUnitElasticity;
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
                Logger.getLogger(CloudServiceElasticityAnalysisEngine.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        Collections.sort(analysisResults, new Comparator<AnalysisResult>() {

            @Override
            public int compare(AnalysisResult o1, AnalysisResult o2) {
                return ((Double) o1.getValue(OVERALL_ELASTICITY)).compareTo(((Double) o2.getValue(OVERALL_ELASTICITY)));
            }

        });
        return analysisResults;
    }

}
