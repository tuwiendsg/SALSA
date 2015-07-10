package at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts;

///**
// * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
// * E184
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package at.ac.tuwien.dsg.cloudofferedservices.concepts;
//
//import org.neo4j.graphdb.RelationshipType;
//
///**
// *
// * @Author Daniel Moldovan
// * @E-mail: d.moldovan@dsg.tuwien.ac.at
// *
// */
//public enum ServiceUnitRelationship implements RelationshipType {
//    //used by CostFunction to show cost for Utility if associated with
//
//    IN_CONJUNCTION_WITH,
//    //use by utility to show the associated resource
//    hasResource,
//    //use by utility and resource to show the associated quality
//    hasQuality,
//    //use by utility to show the associated cost function
//    hasCostFunction,
//    //use by cost function to show the associated cost element
//    hasCostElement,
//    ///used by the utilty to show the different options for a resource or quality
//    hasElasticityCapability,
//    //used by the elasticity characteristic to show for which resource and quality it is specified
//    elasticityCapabilityFor,
//    //use by cloud provider to show the associated utility
//    providesServiceUnit,
//    
//    hasVolatility
//    //use by utility to show if there is another utility to which it must be associated
////    HAS_MANDATORY_ASSOCIATION,
//    //use by utility to show if there is another utility to which it can be optionally associated
////    HAS_OPTIONAL_ASSOCIATION,;
//}
