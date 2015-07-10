/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE;

import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos.ServiceUnitServicesRecommendation;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hungld
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Recommendations")
public class RecommendationSummaries {
    
    @XmlElementWrapper(name = "Recommendations")
    @XmlElement(name = "Recommendation")
    List<RecommendationSummary> summaries = new ArrayList<>();

    public RecommendationSummaries() {
    }

    public List<RecommendationSummary> getSummaries() {
        return summaries;
    }
    
    public RecommendationSummaries hasRecommendation(RecommendationSummary aSummary){
        this.summaries.add(aSummary);
        return this;
    }
        
    @XmlRootElement(name = "Recommendation")
    public static class RecommendationSummary{
        @XmlElement(name = "requirement")
        String requirementName;
        @XmlElement(name = "description")
        String description;
        @XmlElement(name = "provider")
        String provider;
        @XmlElement(name = "details")
        String details;
        

        public RecommendationSummary() {
        }
        public RecommendationSummary(String requirementName, String description, String provider, String details) {
            this.requirementName = requirementName;
            this.description = description;
            this.provider = provider;
            this.details = details;
        }

        public String getRequirementName() {
            return requirementName;
        }

        public String getDescription() {
            return description;
        }        

        public String getProvider() {
            return provider;
        }

        public String getDetails() {
            return details;
        }
        
        
    }
    
    
    public String toXML(){
        StringWriter sw = new StringWriter();
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(RecommendationSummaries.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this, sw);
            return sw.toString();
        } catch (JAXBException ex) {            
            ex.printStackTrace();
            return null;
        }
    }
    
    public RecommendationSummary getFirstRecommendationOfRequirement(String requirementName){
        for (RecommendationSummary r:summaries){
            if (r.getRequirementName().equals(requirementName)){
                return r;
            }
        }
        return null;
    }
    
    public boolean requirementExisted(String requirementName){
        EngineLogger.logger.debug("Checking if a requirement name is existed or not in the Quelle resume");
        for (RecommendationSummary r:summaries){
            EngineLogger.logger.debug("Comparing: summary:" + r.getRequirementName() + " and input:" + requirementName);
            if (r.getRequirementName().equals(requirementName)){
                EngineLogger.logger.debug("Requirement found ! " + requirementName +" with desp: " + r.getDescription() + ", and provider:" + r.getProvider());
                return true;
            }
        }
        return false;
    }
    
    
}
