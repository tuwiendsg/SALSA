/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServiceUnitServicesRecommendation")
public class ServiceUnitServicesRecommendation {

//    @Autowired
//    @XmlTransient
//    private CloudServiceElasticityAnalysisEngine cloudServiceElasticityAnalysisEngine;

    @XmlElement(name = "ServiceUnitRequirements", required = false)
    private Requirements unitReqs;

    @XmlElement(name = "CloudServiceConfigurationRecommendation", required = false)
    private List<CloudServiceConfigurationRecommendation> servicesRecommendations;

    {
        servicesRecommendations = new ArrayList<>();
    }

    @XmlElement(name = "Description", required = false)
    private String description;
    
//    @XmlElement(name = "AverageCostElasticity", required = false)
//    private double averageCostElasticity = 0d;
//    
//    @XmlElement(name = "AverageSUElasticity", required = false)
//    private double averageSUElasticity = 0d;
//   
//    @XmlElement(name = "AverageResourceElasticity", required = false)
//    private double averageResourceElasticity = 0d;
//    
//    @XmlElement(name = "AverageQualityElasticity", required = false)
//    private double averageQualityElasticity = 0d;
//
//    @XmlElement(name = "MinCostElasticity", required = false)
//    private double minCostElasticity = Double.POSITIVE_INFINITY;
//    
//    @XmlElement(name = "MinSUElasticity", required = false)
//    private double minSUElasticity = Double.POSITIVE_INFINITY;
//    
//    @XmlElement(name = "MinResourceElasticity", required = false)
//    private double minResourceElasticity = Double.POSITIVE_INFINITY;
//    
//    @XmlElement(name = "MinQualityElasticity", required = false)
//    private double minQualityElasticity = Double.POSITIVE_INFINITY;
//
//    @XmlElement(name = "MaxCostElasticity", required = false)
//    private double maxCostElasticity = Double.NEGATIVE_INFINITY;
//    
//    @XmlElement(name = "MaxSUElasticity", required = false)
//    private double maxSUElasticity = Double.NEGATIVE_INFINITY;
//    
//    @XmlElement(name = "MaxResourceElasticity", required = false)
//    private double maxResourceElasticity = Double.NEGATIVE_INFINITY;
//    
//    @XmlElement(name = "MaxQualityElasticity", required = false)
//    private double maxQualityElasticity = Double.NEGATIVE_INFINITY;

    public ServiceUnitServicesRecommendation withSolutionRecommendation(Requirements unitReqs, List<CloudServiceConfigurationRecommendation> servicesRecommendations) {
        this.unitReqs = unitReqs;
        this.servicesRecommendations = servicesRecommendations;

        //compute description and elasticity
        updateDescription();
        return this;
    }

    public Requirements getUnitReqs() {
        return unitReqs;
    }

    public void setUnitReqs(Requirements unitReqs) {
        this.unitReqs = unitReqs;
    }

    public List<CloudServiceConfigurationRecommendation> getServicesRecommendations() {
        return servicesRecommendations;
    }

    public void setServicesRecommendations(List<CloudServiceConfigurationRecommendation> servicesRecommendations) {
        this.servicesRecommendations = servicesRecommendations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public double getAverageCostElasticity() {
//        return averageCostElasticity;
//    }
//
//    public void setAverageCostElasticity(double averageCostElasticity) {
//        this.averageCostElasticity = averageCostElasticity;
//    }
//
//    public double getAverageSUElasticity() {
//        return averageSUElasticity;
//    }
//
//    public void setAverageSUElasticity(double averageSUElasticity) {
//        this.averageSUElasticity = averageSUElasticity;
//    }
//
//    public double getAverageResourceElasticity() {
//        return averageResourceElasticity;
//    }
//
//    public void setAverageResourceElasticity(double averageResourceElasticity) {
//        this.averageResourceElasticity = averageResourceElasticity;
//    }
//
//    public double getAverageQualityElasticity() {
//        return averageQualityElasticity;
//    }
//
//    public void setAverageQualityElasticity(double averageQualityElasticity) {
//        this.averageQualityElasticity = averageQualityElasticity;
//    }
//
//    public double getMinCostElasticity() {
//        return minCostElasticity;
//    }
//
//    public void setMinCostElasticity(double minCostElasticity) {
//        this.minCostElasticity = minCostElasticity;
//    }
//
//    public double getMinSUElasticity() {
//        return minSUElasticity;
//    }
//
//    public void setMinSUElasticity(double minSUElasticity) {
//        this.minSUElasticity = minSUElasticity;
//    }
//
//    public double getMinResourceElasticity() {
//        return minResourceElasticity;
//    }
//
//    public void setMinResourceElasticity(double minResourceElasticity) {
//        this.minResourceElasticity = minResourceElasticity;
//    }
//
//    public double getMinQualityElasticity() {
//        return minQualityElasticity;
//    }
//
//    public void setMinQualityElasticity(double minQualityElasticity) {
//        this.minQualityElasticity = minQualityElasticity;
//    }
//
//    public double getMaxCostElasticity() {
//        return maxCostElasticity;
//    }
//
//    public void setMaxCostElasticity(double maxCostElasticity) {
//        this.maxCostElasticity = maxCostElasticity;
//    }
//
//    public double getMaxSUElasticity() {
//        return maxSUElasticity;
//    }
//
//    public void setMaxSUElasticity(double maxSUElasticity) {
//        this.maxSUElasticity = maxSUElasticity;
//    }
//
//    public double getMaxResourceElasticity() {
//        return maxResourceElasticity;
//    }
//
//    public void setMaxResourceElasticity(double maxResourceElasticity) {
//        this.maxResourceElasticity = maxResourceElasticity;
//    }
//
//    public double getMaxQualityElasticity() {
//        return maxQualityElasticity;
//    }
//
//    public void setMaxQualityElasticity(double maxQualityElasticity) {
//        this.maxQualityElasticity = maxQualityElasticity;
//    }
//    
    

    private void updateDescription() {
        description = "";
        int solutionsCount = servicesRecommendations.size();

        for (CloudServiceConfigurationRecommendation solutionConfiguration : servicesRecommendations) {
            //
            // // System.out.println("Matched " +
            // solutionConfiguration.getOverallMatched());
            // // System.out.println("Unmatched " +
            // solutionConfiguration.getOverallUnMatched());
            //
            // String configurationJSONDescription =
            // solutionConfiguration.toJSON().toJSONString();
            // System.out.println(configurationJSONDescription);
//            CloudServiceUnitAnalysisEngine.AnalysisResult analysisResult = cloudServiceElasticityAnalysisEngine.analyzeElasticity(solutionConfiguration.getServiceUnit());
            description += " " + solutionConfiguration.getServiceUnit().getName();

//            double costElasticity = (double) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.COST_ELASTICITY);
//            double sUElasticity = (double) analysisResult
//                    .getValue(CloudServiceElasticityAnalysisEngine.SERVICE_UNIT_ASSOCIATION_ELASTICITY);
//            double resourceElasticity = (double) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.RESOURCE_ELASTICITY);
//            double qualityElasticity = (double) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.QUALITY_ELASTICITY);
//
//            averageCostElasticity += costElasticity;
//            averageSUElasticity += sUElasticity;
//            averageResourceElasticity += resourceElasticity;
//            averageQualityElasticity += qualityElasticity;
//
//            if (minCostElasticity > costElasticity) {
//                minCostElasticity = costElasticity;
//            }
//
//            if (minSUElasticity > sUElasticity) {
//                minSUElasticity = sUElasticity;
//            }
//
//            if (minResourceElasticity > resourceElasticity) {
//                minResourceElasticity = resourceElasticity;
//            }
//
//            if (minQualityElasticity > qualityElasticity) {
//                minQualityElasticity = qualityElasticity;
//            }
//
//            if (maxCostElasticity < costElasticity) {
//                maxCostElasticity = costElasticity;
//            }
//
//            if (maxSUElasticity < sUElasticity) {
//                maxSUElasticity = sUElasticity;
//            }
//
//            if (maxResourceElasticity < resourceElasticity) {
//                maxResourceElasticity = resourceElasticity;
//            }
//
//            if (maxQualityElasticity < qualityElasticity) {
//                maxQualityElasticity = qualityElasticity;
//            }

        }

//        averageCostElasticity /= solutionsCount;
//        averageSUElasticity /= solutionsCount;
//        averageResourceElasticity /= solutionsCount;
//        averageQualityElasticity /= solutionsCount;

    }

}
