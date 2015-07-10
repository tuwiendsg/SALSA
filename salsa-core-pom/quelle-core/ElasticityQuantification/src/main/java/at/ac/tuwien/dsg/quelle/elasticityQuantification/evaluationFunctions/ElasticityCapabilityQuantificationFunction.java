/*
 * Copyright 2014 daniel-tuwien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.evaluationFunctions;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
public class ElasticityCapabilityQuantificationFunction {

    private static final List<Class> elasticityDimensions = new ArrayList<Class>();

    static {
        elasticityDimensions.add(CostFunction.class);
        elasticityDimensions.add(Quality.class);
        elasticityDimensions.add(Resource.class);
        elasticityDimensions.add(CloudOfferedService.class);
    }

    /**
     *
     * @param unit
     * @param weights
     * @param volatilityEvalFunction
     * @param elPhaseEvalFunction
     * @param dependencyEvalFunction
     * @return
     */
    public static Double eval(CloudOfferedService unit, Map<Class, Double> weights, ElasticityVolatilityEvalFunction volatilityEvalFunction,
            ElasticityPhaseEvalFunction elPhaseEvalFunction,
            ElasticityDependencyEvalFunction dependencyEvalFunction) {

        Double result = 0.0;

        for (Class dimension : elasticityDimensions) {
            for (ElasticityCapability capability : unit.getElasticityCapabilities(dimension)) {
                Double dimensionWeight = (weights.containsKey(dimension)) ? weights.get(dimension) : 0.0;
                result += dimensionWeight * ElasticityDimensionQuantificationFunction.eval(capability,
                        volatilityEvalFunction, elPhaseEvalFunction, dependencyEvalFunction);
            }
        }

        return result;

    }

    public static Double eval(CloudOfferedService unit, Class dimension, ElasticityVolatilityEvalFunction volatilityEvalFunction,
            ElasticityPhaseEvalFunction elPhaseEvalFunction,
            ElasticityDependencyEvalFunction dependencyEvalFunction) {

        Double result = 0.0;

        for (ElasticityCapability capability : unit.getElasticityCapabilities(dimension)) {
            result += ElasticityDimensionQuantificationFunction.eval(capability,
                    volatilityEvalFunction, elPhaseEvalFunction, dependencyEvalFunction);
        }

        return result;

    }
}
