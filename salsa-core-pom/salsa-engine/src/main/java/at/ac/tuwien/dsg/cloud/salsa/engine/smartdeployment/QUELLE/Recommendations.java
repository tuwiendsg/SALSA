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
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE;

import at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos.ServiceUnitServicesRecommendation;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The wrapper class for the recommendation results
 * @author Duc-Hung Le
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Recommendations")
public class Recommendations {
    @XmlElement(name = "ServiceUnitServicesRecommendation", type = ServiceUnitServicesRecommendation.class)
    List<ServiceUnitServicesRecommendation> recommendations = new ArrayList<>();
    
    public Recommendations(){}
    
    public Recommendations(List<ServiceUnitServicesRecommendation> recommendations){
        this.recommendations = recommendations;
    }
    
    public List<ServiceUnitServicesRecommendation> getRecommendation(){
        return this.recommendations;
    }
    
    
}
