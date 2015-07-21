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
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.QuelleService;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.RecommendationSummaries;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA.ToscaEnricherCEclipse;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA.ToscaEnricherSALSA;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoredElement;
import at.ac.tuwien.dsg.mela.common.requirements.Condition;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.Strategy;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.StrategyCategory;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TPolicy;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import static java.nio.channels.spi.AsynchronousChannelProvider.provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author Duc-Hung Le
 */
@Service
@Path("/smart")
public class SmartDeploymentService {

    String origineExt = ".original";
    String enrichedExt = ".enriched";
    String requirementExt = ".requirements";

    public enum EnrichFunctions {

        QuelleCloudServiceRecommendation,
        SalsaInfoCompletion
    }

    @POST
    @Path("/tosca/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public boolean submitTosca(String toscaXML, @PathParam("serviceName") String serviceName, @DefaultValue("true") @QueryParam("overwrite") boolean overwrite) {
        String saveAs = SalsaConfiguration.getToscaTemplateStorage() + File.separator + serviceName + origineExt;
        if (new File(saveAs).exists() && overwrite == false) {
            EngineLogger.logger.debug("Do not overwrite file : " + saveAs);
            return false;
        }
        try {
            TDefinitions def = ToscaXmlProcess.readToscaXML(toscaXML);
            ToscaXmlProcess.writeToscaDefinitionToFile(def, saveAs);
        } catch (JAXBException e) {
            EngineLogger.logger.error("Fail to pass the TOSCA !");
            e.printStackTrace();
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot write the TOSCA to disk !");
            ex.printStackTrace();
        }
        EngineLogger.logger.debug("Saved/Updated cloud description in : " + saveAs);
        return true;
    }

    @POST
    @Path("/requirement/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public void submitQuelleRequirementForService(MultiLevelRequirements requirements, @PathParam("serviceName") String serviceName) {
        String saveAs = SalsaConfiguration.getToscaTemplateStorage() + File.separator + serviceName + requirementExt;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(MultiLevelRequirements.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(requirements, new File(saveAs));
        } catch (JAXBException ex) {
            EngineLogger.logger.error("Cannot marshall the multilevel requirements that you submitted !");
            ex.printStackTrace();
        }
    }

    @GET
    @Path("/salsaTosca/enrich/quelle/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public String enrichSalsaTosca(@PathParam("serviceName") String serviceName, @QueryParam("f") String[] listOfFunctionality) {
        List<String> list = Arrays.asList(listOfFunctionality);
        String file = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.enrichedExt;
        if (!(new File(file).exists())) {
            try {
                FileUtils.copyFile(new File(SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.origineExt), new File(file));
            } catch (IOException ex) {
                EngineLogger.logger.error("The original Tosca is not found to copy. Cannot enrich.");
                ex.printStackTrace();
            }
        }

        if (list.contains(EnrichFunctions.SalsaInfoCompletion.toString())) {
            EngineLogger.logger.debug("Start to enrich by using: SalsaComponentCompletement");
            try {
                TDefinitions def = ToscaXmlProcess.readToscaFile(file);
                ToscaEnricherSALSA salsaEnricher = new ToscaEnricherSALSA(def);
                salsaEnricher.enrichHighLevelTosca();
                ToscaXmlProcess.writeToscaDefinitionToFile(def, file);
            } catch (JAXBException | IOException ex) {
                ex.printStackTrace();
            } catch (SalsaException ex) {
                ex.printStackTrace();
            }
        }

        if (list.contains(EnrichFunctions.QuelleCloudServiceRecommendation.toString())) {
            EngineLogger.logger.debug("Start to enrich by using: QuelleCloudServiceRecommendation");
            QuelleService quelleService = new QuelleService();
            // load requirements
            MultiLevelRequirements reqs = loadQuelleRequirement(serviceName);
            RecommendationSummaries sums = quelleService.getRecommendationSummary(reqs);
            EngineLogger.logger.debug("recommendation sums: " + sums.toXML());
            // now load the service description            
            try {
                TDefinitions def = ToscaXmlProcess.readToscaFile(file);
                updateSalsaDescriptionWithQuelleResult(def, sums);
                EngineLogger.logger.debug("Writing down Quelle enriched file");
                ToscaXmlProcess.writeToscaDefinitionToFile(def, file);
                EngineLogger.logger.debug("Writing down Quelle enriched file done");
            } catch (JAXBException | IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return FileUtils.readFileToString(new File(file));
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot read the enriched file. Big problem !");
            ex.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/CAMFTosca/enrich/quelle/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public String enrichCAMFToscaWithQuelle(@PathParam("serviceName") String serviceName, @QueryParam("f") String[] listOfFunctionality) {
        List<String> list = Arrays.asList(listOfFunctionality);
        String file = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.enrichedExt;
        if (!(new File(file).exists())) {
            try {
                FileUtils.copyFile(new File(SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.origineExt), new File(file));
            } catch (IOException ex) {
                EngineLogger.logger.error("The original Tosca is not found to copy. Cannot enrich.");
                ex.printStackTrace();
            }
        }

        if (list.contains(EnrichFunctions.SalsaInfoCompletion.toString())) {
            // TODO: develop this
        }

        if (list.contains(EnrichFunctions.QuelleCloudServiceRecommendation.toString())) {
            EngineLogger.logger.debug("Start to enrich by using: QuelleCloudServiceRecommendation");
            QuelleService quelleService = new QuelleService();
            try {
                TDefinitions def = ToscaXmlProcess.readToscaFile(file);

                MultiLevelRequirements reqs = generateQuelleRequirementFromCAMFTosca(def);
                RecommendationSummaries sums = quelleService.getRecommendationSummary(reqs);
                // load requirements
                EngineLogger.logger.debug("recommendation sums: " + sums.toXML());

                updateCAMFDescriptionWithQuelleResult(def, sums);

                EngineLogger.logger.debug("Writing down Quelle enriched file");

                JAXBContext jaxbContext = JAXBContext.newInstance(TDefinitions.class, CAMFElasticityProperty.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(def, new File(file));

                EngineLogger.logger.debug("Writing down Quelle enriched file done");

            } catch (JAXBException | IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            return FileUtils.readFileToString(new File(file));
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot read the enriched file. Big problem !");
            ex.printStackTrace();
            return null;
        }
    }

    @POST
    @Path("/CAMFTosca/enrich/quelle/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public String enrichCAMFToscaWithQuelle(String toscaXML, @PathParam("serviceName") String serviceName, @QueryParam("f") String[] listOfFunctionality) {
        SmartDeploymentService sds = new SmartDeploymentService();
        sds.submitTosca(toscaXML, serviceName, true);
        return enrichCAMFToscaWithQuelle(serviceName, listOfFunctionality);
    }

    private MultiLevelRequirements loadQuelleRequirement(String serviceName) {
        String saveAs = SalsaConfiguration.getToscaTemplateStorage() + File.separator + serviceName + requirementExt;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(MultiLevelRequirements.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MultiLevelRequirements reqs = (MultiLevelRequirements) jaxbUnmarshaller.unmarshal(new File(saveAs));
            return reqs;
        } catch (JAXBException ex) {
            EngineLogger.logger.error("Cannot marshall the multilevel requirements that you submitted !");
            ex.printStackTrace();
            return null;
        }
    }

    private void updateSalsaDescriptionWithQuelleResult(TDefinitions def, RecommendationSummaries resume) {
        EngineLogger.logger.debug("Starting update Salsa description with QUELLE recommendation");
        List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplatesOfTypeList(SalsaEntityType.SOFTWARE.getEntityTypeString(), def);
        for (TNodeTemplate node : nodes) {
            EngineLogger.logger.debug("Checking node: " + node.getId());
            String infrastructureRequirement = node.getId() + ".IaaS";
            EngineLogger.logger.debug("The requirement name is: " + infrastructureRequirement);
            TNodeTemplate osNode = ToscaStructureQuery.getNodeTemplate_ofOSType_ThatHost(node, def);
            if (osNode != null) {
                EngineLogger.logger.debug("The OS node that hosts node " + node.getId() + " is " + osNode.getId());
                SalsaMappingProperties oldProps = (SalsaMappingProperties) osNode.getProperties().getAny();
                if (oldProps == null) {
                    oldProps = new SalsaMappingProperties();
                }
                if (resume.requirementExisted(infrastructureRequirement)) {
                    SalsaMappingProperties props = new SalsaMappingProperties();
                    props.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "provider", resume.getFirstRecommendationOfRequirement(infrastructureRequirement).getProvider());
                    String des = resume.getFirstRecommendationOfRequirement(infrastructureRequirement).getDescription() + " ";
                    des = des.substring(0, des.indexOf(" ")).trim();
                    props.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "instanceType", des);
                    if (props.get(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "provider").equals(oldProps.get(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "provider"))) {
                        props.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "baseImage", oldProps.get(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "baseImage"));
                    }
                    props.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "packages", oldProps.get(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), "packages"));
                    osNode.getProperties().setAny(props);
                }
            }
        }
    }

    private void updateOSNodeConfig(TTopologyTemplate topo) {
        List<TEntityTemplate> entities = topo.getNodeTemplateOrRelationshipTemplate();
        boolean found = false;
        for (TEntityTemplate enti : entities) {
            if (enti.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                TNodeTemplate node = (TNodeTemplate) enti;
                SalsaMappingProperties maps;
                if (node.getProperties() == null) {
                    maps = new SalsaMappingProperties();
                } else {
                    maps = (SalsaMappingProperties) node.getProperties().getAny();
                    if (maps == null) {
                        maps = new SalsaMappingProperties();
                    }
                    found = false;
                    for (SalsaMappingProperties.SalsaMappingProperty imap : maps.getProperties()) {
                        if (imap.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                            found = true;
                        }
                    }
                    if (found) {
                        continue;
                    }
                }

                if (!found) {
                    System.out.println("Start write out some configuration for node: " + enti.getId());
                    // can put some optimization processing here, now get the default values
                    InputStream input = ToscaEnricherSALSA.class.getResourceAsStream("/cloudconfig.default.properties");
                    java.util.Properties prop = new java.util.Properties();
                    try {
                        prop.load(input);
                    } catch (IOException e) {
                        EngineLogger.logger.error("Couldn't find the default cloud configuration file.");
                        return;
                    }
                    String provider = prop.getProperty("provider");
                    String instanceType = prop.getProperty("instanceType");
                    String baseImage = prop.getProperty("baseImage");
                    String packages = "";

                    System.out.println("Write the configuration: " + provider + ", " + instanceType + ", " + baseImage);

                    Map<String, String> map = new HashMap<>();
                    map.put("provider", provider);
                    map.put("instanceType", instanceType);
                    map.put("baseImage", baseImage);
                    map.put("packages", packages);
                    maps.put(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString(), map);

                    EngineLogger.logger.debug("Map of properties is created");
                    node.getProperties().setAny(maps);
                    node.setMinInstances(1);
                    node.setMaxInstances("unbounded");
                    EngineLogger.logger.debug("Done adding configuration");
                } //if !found
            }
        }
    }

    // generate requirement and save
    public MultiLevelRequirements generateQuelleRequirementFromCAMFTosca(TDefinitions def) {        // get all Node policy

        String serviceName = ToscaStructureQuery.getServiceTemplateList(def).get(0).getName();
        MultiLevelRequirements serviceReq = new MultiLevelRequirements(MonitoredElement.MonitoredElementLevel.SERVICE);
        serviceReq.setName(serviceName);

        // get Strategy
        TServiceTemplate st = ToscaStructureQuery.getServiceTemplateList(def).get(0);

        Strategy newStra = new Strategy();
        newStra.setStrategyCategory(StrategyCategory.OVERALL_REQUIREMENTS);
        serviceReq.addStrategy(newStra);
        newStra.setStrategyCategory(StrategyCategory.MINIMUM_COST);
        serviceReq.addStrategy(newStra);
        newStra.setStrategyCategory(StrategyCategory.MINIMUM_RESOURCES);
        serviceReq.addStrategy(newStra);

        // add requirement
        MultiLevelRequirements topoReq = new MultiLevelRequirements(MonitoredElement.MonitoredElementLevel.SERVICE_TOPOLOGY);
        topoReq.setName(serviceName + "topo");
        serviceReq.addMultiLevelRequirements(topoReq);

        // GET ALL NODE's POLICIES
        List<TNodeTemplate> nodeTemplates = ToscaStructureQuery.getNodeTemplateList(def);
        for (TNodeTemplate node : nodeTemplates) {
            if (node.getPolicies() != null) {
                EngineLogger.logger.debug("Found policies of node: " + node.getId() + "/" + node.getName());
                List<TPolicy> policies = node.getPolicies().getPolicy();
                MultiLevelRequirements suReq = new MultiLevelRequirements(MonitoredElement.MonitoredElementLevel.SERVICE_UNIT);
                suReq.setName(node.getName());
                topoReq.addMultiLevelRequirements(suReq);
                Requirements reqs = new Requirements();
                reqs.setName(node.getName() + ".IaaS");
                suReq.addRequirements(reqs);
                for (TPolicy p : policies) {
                    if (p.getPolicyType().getLocalPart().equals("Requirement") && p.getPolicyType().getPrefix().equals("smart")) {
                        String theReq = p.getName();
                        EngineLogger.logger.debug("Parsing the requirement: " + theReq);
                        // E>G> CONSTRAINT  Memory&amp;gt;2048
                        String keyword = theReq.split(" ", 2)[0].trim();

                        if (keyword.equals("CONSTRAINT")) {
                            String constraint = theReq.split(" ", 2)[1].trim();
                            String metric = constraint.split("&", 2)[0].trim();
                            String operation = constraint.substring(constraint.indexOf("&") + 1, constraint.lastIndexOf(";"));
                            String value = constraint.substring(constraint.lastIndexOf(";") + 1);
                            EngineLogger.logger.debug("Parsing SYBL directive: Node:" + node.getId() + "/" + node.getName());
                            EngineLogger.logger.debug("Parsing SYBL directive:  - Policy:" + p.getName());
                            EngineLogger.logger.debug("Parsing SYBL directive:  - Keyword:" + constraint);
                            EngineLogger.logger.debug("Parsing SYBL directive:  - metric:" + metric);
                            EngineLogger.logger.debug("Parsing SYBL directive:  - operation:" + operation);
                            EngineLogger.logger.debug("Parsing SYBL directive:  - value:" + value);

                            Condition.Type type;
                            switch (operation) {
                                case "lt":
                                    type = Condition.Type.LESS_THAN;
                                    break;
                                case "le":
                                    type = Condition.Type.LESS_EQUAL;
                                    break;
                                case "gt":
                                    type = Condition.Type.GREATER_THAN;
                                    break;
                                case "ge":
                                    type = Condition.Type.GREATER_EQUAL;
                                    break;
                                case "eq":
                                    type = Condition.Type.EQUAL;
                                    break;
                                default:
                                    type = Condition.Type.EQUAL;
                                    break;
                            }
                            Requirement req = new Requirement(node.getName() + "_req_" + metric);
                            req.addTargetServiceID(node.getName());
                            MetricValue mValue = new MetricValue(value);
                            if (StringUtils.isNumeric(value)) {
                                mValue.setValueType(MetricValue.ValueType.NUMERIC);
                                int numberValue = Integer.parseInt(value);
                                mValue = new MetricValue(numberValue);
                            }

                            req.addCondition(new Condition(type, new Metric(metric), mValue));
                            req.setMetric(new Metric(metric));

                            req.setId(UUID.randomUUID().toString());

                            topoReq.addRequirement(req);
                            reqs.addRequirement(req);

                        }
                    }

                }

            } // end node.getPolicies() != null
        }   // end For

        // try to save it, useless but ya
        JAXBContext jaxbContext;
        try {
            String saveReq = SalsaConfiguration.getToscaTemplateStorage() + File.separator + serviceName + requirementExt;
            EngineLogger.logger.debug("Requirement is generated, trying to save to: " + saveReq);
            jaxbContext = JAXBContext.newInstance(MultiLevelRequirements.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(serviceReq, new File(saveReq));
        } catch (JAXBException ex) {
            Logger.getLogger(SmartDeploymentService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return serviceReq;

    }

    private void updateCAMFDescriptionWithQuelleResult(TDefinitions def, RecommendationSummaries resume) {
        EngineLogger.logger.debug("Starting update CAMF description with QUELLE recommendation");
        List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(def);
        for (TNodeTemplate node : nodes) {
            EngineLogger.logger.debug("Checking node: " + node.getId());
            RecommendationSummaries.RecommendationSummary flavorStr = resume.getFirstRecommendationOfRequirement(node.getName() + ".IaaS");
            EngineLogger.logger.debug("flavorStr: " + flavorStr);
            if (flavorStr != null) {
                CAMFElasticityProperty flavorProp = new CAMFElasticityProperty(flavorStr.getDescription().trim());
                node.getProperties().setAny(flavorProp);
            }
        }
    }

}
