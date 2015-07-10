/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author hungld
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "NodeProperties")
public class CAMFElasticityProperty {
    @XmlElement(name = "Flavor")
    String flavor;

    public CAMFElasticityProperty() {
    }

    public CAMFElasticityProperty(String flavor) {
        this.flavor = flavor;
    }

    public String getFlavor() {
        return flavor;
    }
    
    
}
