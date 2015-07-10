/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author hungld
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
