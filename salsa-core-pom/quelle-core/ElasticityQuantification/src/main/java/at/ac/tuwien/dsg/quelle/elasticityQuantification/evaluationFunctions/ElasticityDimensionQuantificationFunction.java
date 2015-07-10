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

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability.Dependency;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
public class ElasticityDimensionQuantificationFunction {

    public static double eval(ElasticityCapability capability, ElasticityVolatilityEvalFunction volatilityEvalFunction,
            ElasticityPhaseEvalFunction elPhaseEvalFunction,
            ElasticityDependencyEvalFunction dependencyEvalFunction) {

        Double dependenciesEvalResult = 0.0;

        for (Dependency dependency : capability.getCapabilityDependencies()) {
            dependenciesEvalResult += volatilityEvalFunction.eval(dependency.getVolatility()) * dependencyEvalFunction.eval(dependency.getDependencyType());
        }
        return elPhaseEvalFunction.eval(capability.getPhase()) * dependenciesEvalResult;
    }
}
