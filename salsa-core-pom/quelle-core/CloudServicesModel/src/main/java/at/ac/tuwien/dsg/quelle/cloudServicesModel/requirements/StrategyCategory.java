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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Category")
@XmlEnum
public enum StrategyCategory implements Serializable {

//    elasticity strategies
    @XmlEnumValue("Elasticity.OVERALL")
    OVERALL_ELASTICITY,
    
    @XmlEnumValue("Elasticity.QUALITY")
    QUALITY_ELASTICITY,
    
    @XmlEnumValue("Elasticity.COST")
    COST_ELASTICITY,
    
    @XmlEnumValue("Elasticity.RESOURCES")
    RESOURCE_ELASTICITY,
    
    @XmlEnumValue("Elasticity.SERVICE_UNITS_ASSOCIATIONS")
    SERVICE_UNITS_ASSOCIATIONS_ELASTICITY,
    
//    requirements fulfilled based strategies
    
    @XmlEnumValue("Requirements.OVERALL")
    OVERALL_REQUIREMENTS,
    
     @XmlEnumValue("Requirements.QUALITY")
    QUALITY_REQUIREMENTS,
    
    @XmlEnumValue("Requirements.COST")
    COST_REQUIREMENTS,
    
    @XmlEnumValue("Requirements.RESOURCES")
    RESOURCE_REQUIREMENTS,
    
//    property based strategies
     @XmlEnumValue("Properties.QUALITY")
    QUALITY_PROPERTIES,
    
    @XmlEnumValue("Properties.COST")
    COST_PROPERTIES,
    
    @XmlEnumValue("Properties.MINIMUM_RESOURCES")
    MINIMUM_RESOURCES ,
    
    @XmlEnumValue("Properties.MINIMUM_COST")
    MINIMUM_COST
    
}
