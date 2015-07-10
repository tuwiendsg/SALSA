/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE;

import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.RecommendationSummaries.RecommendationSummary;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.Strategy;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos.CloudServiceConfigurationRecommendation;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.dtos.ServiceUnitServicesRecommendation;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.CloudServiceElasticityAnalysisEngine;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.CloudServiceUnitAnalysisEngine;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.RequirementsMatchingEngine;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.engines.ServiceUnitComparators;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.RequirementsResolutionResult;
import at.ac.tuwien.dsg.quelle.elasticityQuantification.requirements.ServiceUnitConfigurationSolution;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * This service integrate with QUELLE to
 *
 * @author hungld
 */
@Service
@Path("/quelle")
public class QuelleService {

    private final RequirementsMatchingEngine requirementsMatchingEngine = new RequirementsMatchingEngine();
    private final ServiceUnitComparators serviceUnitComparators = new ServiceUnitComparators();
    private final String cloudDescriptionFileExtension = ".cloud.data";

    @GET
    @Path("/health")
    public String health() {
        return "Quelle is healthy";
    }

    @POST
    @Path("/recommend")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Recommendations getRecommendation(MultiLevelRequirements multiLevelRequirements) {
        EngineLogger.logger.debug("Getting recommendation for multiple requirement: " + multiLevelRequirements.getName());
        
        List<MultiLevelRequirements> individualServiceUnitRequirements = multiLevelRequirements.flatten();
        List<ServiceUnitServicesRecommendation> recommendations = new ArrayList<>();

        // load cloud provider
        List<CloudProvider> cloudProviders = loadCloudAllDescription();
        EngineLogger.logger.debug("Loaded "+cloudProviders.size()+" provider done.");
        if (cloudProviders.isEmpty()){
            EngineLogger.logger.error("Do not found any cloud provider infomation. Please submit at least one first !");            
            return null;
        }

        for (MultiLevelRequirements reqs : individualServiceUnitRequirements) {
            RequirementsResolutionResult requirementsMatchingResult = requirementsMatchingEngine.analyzeMultiLevelRequirements(cloudProviders, reqs);
            Map<MultiLevelRequirements, Map<Requirements, List<ServiceUnitConfigurationSolution>>> bestElasticity = requirementsMatchingResult.getConcreteConfigurations(serviceUnitComparators);
            List<CloudServiceConfigurationRecommendation> recommendedConfigurations = new ArrayList<>();
            {
                for (MultiLevelRequirements levelRequirements : bestElasticity.keySet()) {
                    Map<Requirements, List<ServiceUnitConfigurationSolution>> solutions = bestElasticity.get(levelRequirements);
                    String strategies = "";
                    for (Strategy s : levelRequirements.getOptimizationStrategies()) {
                        strategies += "_" + s.getStrategyCategory();
                    }

                    for (Requirements requirements : solutions.keySet()) {
                        String solutionsNames = "";

                        int solutionsCount = solutions.get(requirements).size();

                        // compute average elasticities
                        double averageCostElasticity = 0d;
                        double averageSUElasticity = 0d;
                        double averageResourceElasticity = 0d;
                        double averageQualityElasticity = 0d;

                        double minCostElasticity = Double.POSITIVE_INFINITY;
                        double minSUElasticity = Double.POSITIVE_INFINITY;
                        double minResourceElasticity = Double.POSITIVE_INFINITY;
                        double minQualityElasticity = Double.POSITIVE_INFINITY;

                        double maxCostElasticity = Double.NEGATIVE_INFINITY;
                        double maxSUElasticity = Double.NEGATIVE_INFINITY;
                        double maxResourceElasticity = Double.NEGATIVE_INFINITY;
                        double maxQualityElasticity = Double.NEGATIVE_INFINITY;

                        for (ServiceUnitConfigurationSolution solutionConfiguration : solutions.get(requirements)) {

                            CloudServiceUnitAnalysisEngine cloudServiceElasticityAnalysisEngine = new CloudServiceUnitAnalysisEngine();
                            CloudServiceUnitAnalysisEngine.AnalysisResult analysisResult = cloudServiceElasticityAnalysisEngine.analyzeElasticity(solutionConfiguration.getServiceUnit());
                            solutionsNames += " " + solutionConfiguration.getServiceUnit().getName();

                            double costElasticity = (Integer) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.COST_ELASTICITY);
                            double sUElasticity = (Integer) analysisResult
                                    .getValue(CloudServiceElasticityAnalysisEngine.SERVICE_UNIT_ASSOCIATION_ELASTICITY);
                            double resourceElasticity = (Integer) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.RESOURCE_ELASTICITY);
                            double qualityElasticity = (Integer) analysisResult.getValue(CloudServiceElasticityAnalysisEngine.QUALITY_ELASTICITY);

                            averageCostElasticity += costElasticity;
                            averageSUElasticity += sUElasticity;
                            averageResourceElasticity += resourceElasticity;
                            averageQualityElasticity += qualityElasticity;

                            if (minCostElasticity > costElasticity) {
                                minCostElasticity = costElasticity;
                            }

                            if (minSUElasticity > sUElasticity) {
                                minSUElasticity = sUElasticity;
                            }

                            if (minResourceElasticity > resourceElasticity) {
                                minResourceElasticity = resourceElasticity;
                            }

                            if (minQualityElasticity > qualityElasticity) {
                                minQualityElasticity = qualityElasticity;
                            }

                            if (maxCostElasticity < costElasticity) {
                                maxCostElasticity = costElasticity;
                            }

                            if (maxSUElasticity < sUElasticity) {
                                maxSUElasticity = sUElasticity;
                            }

                            if (maxResourceElasticity < resourceElasticity) {
                                maxResourceElasticity = resourceElasticity;
                            }

                            if (maxQualityElasticity < qualityElasticity) {
                                maxQualityElasticity = qualityElasticity;
                            }
                            recommendedConfigurations.add(new CloudServiceConfigurationRecommendation().withServiceUnitConfigurationSolution(requirements.getName(),
                                    solutionConfiguration, costElasticity, sUElasticity, resourceElasticity, qualityElasticity));

//                            ObjectMapper mapper = new ObjectMapper();
//                            System.out.println("solutionConfiguration" + mapper.writeValueAsString(solutionConfiguration));
//                            System.out.println("recommendedConfigurations: " + mapper.writeValueAsString(recommendedConfigurations));                            
                            recommendations.add(new ServiceUnitServicesRecommendation().withSolutionRecommendation(requirements, recommendedConfigurations));

                        }

//                        averageCostElasticity /= solutionsCount;
//                        averageSUElasticity /= solutionsCount;
//                        averageResourceElasticity /= solutionsCount;
//                        averageQualityElasticity /= solutionsCount;
//                        System.out.println(requirements.getName() + "," + strategies + "," + solutionsNames + "," + solutionsCount + "," + averageCostElasticity + ","
//                                + minCostElasticity + "," + maxCostElasticity + "," + averageSUElasticity + "," + minSUElasticity + "," + maxSUElasticity
//                                + "," + averageResourceElasticity + "," + minResourceElasticity + "," + maxResourceElasticity + ","
//                                + averageQualityElasticity + "," + minQualityElasticity + "," + maxQualityElasticity);
//                        System.out.println("\n");
                    }

                }
            }
        }
        EngineLogger.logger.debug("Quelle recommendation is done, returning result ....");
        // wrapping
        return new Recommendations(recommendations);
        
//        File saveAs = new File("/tmp/quelleresult");
//        Writer result = new StringWriter();
//        JAXBContext jaxbContext;
//        try {
//            jaxbContext = JAXBContext.newInstance(Recommendations.class);
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.marshal(new Recommendations(recommendations), saveAs);
//            
//            jaxbMarshaller.marshal(new Recommendations(recommendations), result);
//        } catch (JAXBException ex) {
//            ex.printStackTrace();
//        }             
//        return result.toString();
    }
    
    @POST
    @Path("/recommend/summary")
//    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public RecommendationSummaries getRecommendationSummary
        (MultiLevelRequirements multiLevelRequirements) {
        EngineLogger.logger.debug("Summarizing the recommendation...");
        List<ServiceUnitServicesRecommendation> recommendations = getRecommendation(multiLevelRequirements).getRecommendation();
        RecommendationSummaries sums = new RecommendationSummaries();        
        
        List<CloudProvider> providers = loadCloudAllDescription();
        
        for (ServiceUnitServicesRecommendation recom: recommendations){
            EngineLogger.logger.debug("Found one recommendation. SU: " + recom.getUnitReqs().getName() +", description:" + recom.getDescription());
            String uuid = recom.getServicesRecommendations().get(0).getServiceUnit().getUuid().toString();
            CloudOfferedService ofs = findCloudOfferedServiceByUUID(providers,uuid);
            CloudProvider p = findProviderThatOfferServiceWithUUID(providers,uuid);      
            String details="";
            for (CloudServiceConfigurationRecommendation cloudRecom: recom.getServicesRecommendations()){
                
            }
            sums.hasRecommendation(new RecommendationSummary(recom.getUnitReqs().getName(), ofs.getName(), p.getName(), details));
        }
        return sums;
    }

    @POST
    @Path("/submitCloudDescription")
    @Consumes(MediaType.APPLICATION_XML)
    public boolean submitCloudProviderDescription(CloudProvider provider, @DefaultValue("true") @QueryParam("overwrite") boolean overwrite) {
        File saveAs = new File(SalsaConfiguration.getCloudProviderDescriptionDir() + File.separator + provider.getName() + cloudDescriptionFileExtension);
        if (saveAs.exists() && overwrite == false) {
            EngineLogger.logger.debug("Do not overwrite file : " + saveAs);
            return false;
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CloudProvider.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(provider, saveAs);
        } catch (JAXBException e) {
            EngineLogger.logger.debug("Fail to pass the cloud description !");
            e.printStackTrace();
        }
        EngineLogger.logger.debug("Saved/Updated cloud description in : " + saveAs);
        return true;
    }

    public List<CloudProvider> loadCloudAllDescription() {
        List<CloudProvider> providers = new ArrayList<>();
        CloudDataExtensionFilter filter = new CloudDataExtensionFilter(cloudDescriptionFileExtension);
        File dir = new File(SalsaConfiguration.getCloudProviderDescriptionDir());
        if (dir.isDirectory() == false) {
            EngineLogger.logger.debug("Error: Cannot find the directory storing cloud descriptions");
            return null;
        }
        String[] list = dir.list(filter);
        if (list.length == 0) {
            EngineLogger.logger.debug("No file with extension : " + cloudDescriptionFileExtension + " is found. No cloud provider description is load.");
            return null;
        }
        for (String file : list) {
            String temp = new StringBuffer(SalsaConfiguration.getCloudProviderDescriptionDir()).append(File.separator).append(file).toString();
            EngineLogger.logger.debug("Loading cloud description in file: " + temp);

            File loadingFile = new File(temp);
            JAXBContext jaxbContext;
            try {
                jaxbContext = JAXBContext.newInstance(CloudProvider.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                CloudProvider provider = (CloudProvider) jaxbUnmarshaller.unmarshal(loadingFile);
                providers.add(provider);
            } catch (JAXBException ex) {
                EngineLogger.logger.debug("Cannot load cloud description file: " + temp);
                ex.printStackTrace();
            }
        }
        return providers;
    }
    
    
    public CloudProvider loadSpecificCloudProvider(String name){
        File dir = new File(SalsaConfiguration.getCloudProviderDescriptionDir());
        CloudDataExtensionFilter filter = new CloudDataExtensionFilter(cloudDescriptionFileExtension);
        if (dir.isDirectory() == false) {
            EngineLogger.logger.debug("Error: Cannot find the directory storing cloud descriptions");
            return null;
        }
        String[] list = dir.list(filter);
        if (list.length == 0) {
            EngineLogger.logger.debug("No file with extension : " + cloudDescriptionFileExtension + " is found. No cloud provider description is load.");
            return null;
        }
        for (String file : list) {
            String temp = new StringBuffer(SalsaConfiguration.getCloudProviderDescriptionDir()).append(File.separator).append(file).toString();
            EngineLogger.logger.debug("Loading cloud description in file: " + temp);

            File loadingFile = new File(temp);
            JAXBContext jaxbContext;
            try {
                jaxbContext = JAXBContext.newInstance(CloudProvider.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                CloudProvider provider = (CloudProvider) jaxbUnmarshaller.unmarshal(loadingFile);
                if (provider.getName().equals(name)){
                    EngineLogger.logger.debug("Found cloud provider with name: " + name +". Load file: " + temp);
                    return provider;
                } else {
                    EngineLogger.logger.debug("Loaded file: " + temp +", but the provider name is "+provider.getName()+" is not what we are looking for: " + name);
                }
            } catch (JAXBException ex) {
                EngineLogger.logger.debug("Cannot load cloud description file: " + temp);
                ex.printStackTrace();
            }
        }
        EngineLogger.logger.debug("Found no cloud provider with name: " + name);
        return null;
    }
    

    // inner class, .cloud.data extension filter
    private class CloudDataExtensionFilter implements FilenameFilter {

        private final String ext;

        public CloudDataExtensionFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
    
    
    private CloudOfferedService findCloudOfferedServiceByUUID(List<CloudProvider> providers, String uuid){
        for (CloudProvider p: providers){
            for (CloudOfferedService ofs : p.getCloudOfferedServices()){
                if (ofs.getUuid().toString().equals(uuid)){
                    return ofs;
                }
            }
        }
        return null;
    }
    
    private CloudProvider findProviderThatOfferServiceWithUUID(List<CloudProvider> providers, String uuid){
        for (CloudProvider p: providers){
            for (CloudOfferedService ofs : p.getCloudOfferedServices()){
                if (ofs.getUuid().toString().equals(uuid)){
                    return p;
                }
            }
        }
        return null;
    }
    
    
}
