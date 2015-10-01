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
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.QuelleService;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.RecommendationSummaries;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA.ToscaEnricherSALSA;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MonitoredElement;
import at.ac.tuwien.dsg.mela.common.requirements.Condition;
import at.ac.tuwien.dsg.mela.common.requirements.Requirement;
import at.ac.tuwien.dsg.mela.common.requirements.Requirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.Strategy;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.StrategyCategory;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TPolicy;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.antlr.v4.runtime.ANTLRInputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;

import org.eclipse.camf.carl.antlr4.CARLLexer;
import org.eclipse.camf.carl.antlr4.CARLParser;
import org.eclipse.camf.carl.antlr4.CARLParser.RequirementsContext;
import org.eclipse.camf.carl.antlr4.CARLProgramListener;
import org.eclipse.camf.carl.model.CPURequirement;
import org.eclipse.camf.carl.model.DiskRequirement;
import org.eclipse.camf.carl.model.IRequirement;
import org.eclipse.camf.carl.model.MemoryRequirement;
import org.eclipse.camf.carl.model.NetworkRequirement;
import org.eclipse.camf.carl.model.OSRequirement;
import org.eclipse.camf.carl.model.RangeAttribute;
import org.eclipse.camf.carl.model.RequirementCategory;
import org.eclipse.camf.carl.model.SoftwareRequirement;
import org.eclipse.camf.carl.model.SystemRequirement;
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
            EngineLogger.logger.error("Fail to pass the TOSCA !", e);
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot write the TOSCA to disk !", ex);
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
    @Path("/salsaTosca/enrich/{serviceName}")
    @Consumes(MediaType.APPLICATION_XML)
    public String enrichSalsaTosca(@PathParam("serviceName") String serviceName, @QueryParam("f") String[] listOfFunctionality) {
        List<String> list = Arrays.asList(listOfFunctionality);
        String file = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.enrichedExt;
        if (!(new File(file).exists())) {
            try {
                FileUtils.copyFile(new File(SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + this.origineExt), new File(file));
            } catch (IOException ex) {
                EngineLogger.logger.error("The original Tosca is not found to copy. Cannot enrich.", ex);
            }
        }

        if (list.contains(EnrichFunctions.SalsaInfoCompletion.toString())) {
            EngineLogger.logger.debug("Start to enrich by using: SalsaComponentCompletement");
            try {
                TDefinitions def = ToscaXmlProcess.readToscaFile(file);
                ToscaEnricherSALSA salsaEnricher = new ToscaEnricherSALSA(def);
                salsaEnricher.enrichHighLevelTosca();
                ToscaXmlProcess.writeToscaDefinitionToFile(def, file);
            } catch (JAXBException | IOException | SalsaException ex) {
                EngineLogger.logger.error("Error when reading TOSCA to enrich.", ex);
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
                EngineLogger.logger.error("Error when enriching TOSCA.", ex);
            }
        }
        try {
            return FileUtils.readFileToString(new File(file));
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot read the enriched file. Big problem !", ex);
            return null;
        }
    }

    @POST
    @Path("/CAMFTosca/enrich/CSAR/{serviceName}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public String enrich_CAMF_CSAR(byte[] fileBytes, @PathParam("serviceName") String serviceName){
        SalsaConfiguration.getArtifactStorage();
        // retrieve the CSAR
        String csarTmp = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + ".csar";
        try {
            FileUtils.writeByteArrayToFile(new File(csarTmp), fileBytes);
            return enrich_CAMF_CSAR_Process(csarTmp, serviceName);
        } catch (IOException ex) {
            EngineLogger.logger.error("Fail to get byteArray of CSAR file to save to {}", csarTmp,ex);
            return null;
        }
    }
    
    
    // This is the MAIN class which received a CSAR and return a enriched CSAR
    @POST
    @Path("/CAMFTosca/enrich/CSAR/{serviceName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String enrich_CAMF_CSAR(String csarURL, @PathParam("serviceName") String serviceName) {
        SalsaConfiguration.getArtifactStorage();
        // download the CSAR
        String csarTmp = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + ".csar";
        try {
            FileUtils.copyURLToFile(new URL(csarURL), new File(csarTmp));
            return enrich_CAMF_CSAR_Process(csarTmp, serviceName);
        } catch (IOException ex) {
            EngineLogger.logger.error("Fail to download CSAR file at URL: {} and save to {}", csarURL, csarTmp,ex);
            return null;
        }
    }
    
    private String enrich_CAMF_CSAR_Process(String csarTmp, String serviceName){
        String extractedFolder = csarTmp + ".extracted";
        String toscaFile = extractedFolder + "/Definitions/Application.tosca";
        String scriptDir = extractedFolder + "/Scripts/";
        try {
            // extract CSAR
            CSARParser.extractCsar(new File(csarTmp), extractedFolder);

            // enrich with QUELLE for
            String toscaXML = FileUtils.readFileToString(new File(toscaFile));
            EngineLogger.logger.debug("Read tosca string done. 100 first characters: {}", toscaXML);
            EngineLogger.logger.debug("Now trying to enrich with QUELLE....");
            //enrichCAMFToscaWithQuelle(toscaXML, serviceName, new String[]{EnrichFunctions.QuelleCloudServiceRecommendation.toString(), EnrichFunctions.SalsaInfoCompletion.toString()});
            SmartDeploymentService sds = new SmartDeploymentService();
            String result = sds.enrichCAMFToscaWithQuelle(toscaXML, serviceName, new String[]{EnrichFunctions.QuelleCloudServiceRecommendation.toString()});
            EngineLogger.logger.debug("After enrich with QUELLE, the result is: {}", result);
            // write back to right place
            FileUtils.writeStringToFile(new File(toscaFile), result);

            // read software requirement in TOSCA for each node, put in a map + artifact
            // a map between node ID and full requirement in Tag
            Map<String, String> allRequirements = new HashMap<>();
            TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
            for (TNodeTemplate node : ToscaStructureQuery.getNodeTemplateList(def)) {
                EngineLogger.logger.debug("Checking node: {}", node.getId());
                String policiesStr = new String();
                if (node.getPolicies() != null) {
                    EngineLogger.logger.debug("Found policies of node: " + node.getId() + "/" + node.getName());
                    List<TPolicy> policies = node.getPolicies().getPolicy();
                    for (TPolicy p : policies) {
                        if (p.getPolicyType().getLocalPart().equals("Requirement") && p.getPolicyType().getPrefix().equals("SmartDeployment")) {
                            if (p.getName().startsWith("CONSTRAINT")) {
                                // TODO: parse SYBL policies
                            } else {
                                policiesStr += p.getName().trim();
                                if (!p.getName().trim().endsWith(";")) {
                                    policiesStr += ";";
                                    EngineLogger.logger.debug("polociesStr = {}", policiesStr);
                                }
                            }
                        }
                    }
                }
                EngineLogger.logger.debug("Collected policies for node {} is : {}", node.getId(), policiesStr);
                allRequirements.put(node.getId(), policiesStr);
            }
            EngineLogger.logger.debug("In total, we got following requirements: " + allRequirements.toString());

            // Load dependency graph knowledge base
            String dependencyDataFile = SmartDeploymentService.class.getResource("/data/salsa.dependencygraph.xml").getFile();
            SalsaStackDependenciesGraph depGraph = SalsaStackDependenciesGraph.fromXML(FileUtils.readFileToString(new File(dependencyDataFile)));

            // ENRICH SCRIPT
            // extract all the requirement, put into the hashmap
            for (Map.Entry<String, String> entry : allRequirements.entrySet()) {
                EngineLogger.logger.debug("Analyzing node: {}. Full policies string is: *** {} ***", entry.getKey(), entry.getValue());

                // extract CARL Strings
                CharStream stream = new ANTLRInputStream(entry.getValue());
                CARLLexer lexer = new CARLLexer(stream);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CARLParser parser = new CARLParser(tokens);
                RequirementsContext requirementsContext = parser.requirements();

                ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
                CARLProgramListener extractor = new CARLProgramListener(parser);
                walker.walk(extractor, requirementsContext); // initiate walk of tree with listener    
                org.eclipse.camf.carl.model.Requirements requirements = extractor.getRequirements();

                HashMap<String, String> allReqsOfNode = new HashMap<>();
                ArrayList<String> checkList = new ArrayList<>();
                // os=Ubuntu; os:ver=12.04; sw=jre:1.7 ==> os=Ubuntu, 
                // here flat all the requirement of the node
                for (IRequirement req : requirements.getRequirements()) {
                    EngineLogger.logger.debug("Irequirement: " + req.toString());
                    if (req.getCategory().equals(RequirementCategory.SOFTWARE)) {
                        SoftwareRequirement swr = (SoftwareRequirement) req;
                        allReqsOfNode.put("sw", removeQuote(swr.getName()));
                        allReqsOfNode.put(removeQuote(swr.getName()) + ":ver", swr.getVersion().getVersion());
                        checkList.add(swr.getName());
                    } else {
                        if (req.getCategory().equals(RequirementCategory.OPERATING_SYSTEM)) { // the system part is generated by quelle
                            OSRequirement osReq = (OSRequirement) req;
                            if (osReq.getName() != null) {
                                allReqsOfNode.put("os", removeQuote(osReq.getName()));
                            }
                            if (osReq.getVersion() != null) {
                                allReqsOfNode.put("os:ver", osReq.getVersion().getVersion());
                            }

                        }
                    }
                }
                // find all the deploymet script of all "sw" requirements
                LinkedList<String> listOfScripts = new LinkedList<>();
                EngineLogger.logger.debug("The node {} will be enriched based-on the requirements: {}", entry.getKey(), checkList.toString());
                for (String swReq : checkList) {
                    EngineLogger.logger.debug("Searching deployment script for software req: {}", swReq);
                    SalsaStackDependenciesGraph theNode = depGraph.findNodeByName(swReq);
                    EngineLogger.logger.debug("Node found: {}", theNode.getName());
                    EngineLogger.logger.debug("All requirements: {}", allReqsOfNode.toString());

                    LinkedList<String> tmp = theNode.searchDeploymentScriptTemplate(allReqsOfNode);
                    if (tmp != null) {
                        listOfScripts.addAll(tmp);
                    }
                }
                EngineLogger.logger.debug(listOfScripts.toString());

                // create a script to solve all dependencies first
                String nodeID = entry.getKey();
                String theDependencyScript = "#!/bin/bash \n\n######## Generated by the Decision Module to solve the software dependencies ######## \n\n";
                for (String appendScript : listOfScripts) {
                    String theAppend = SmartDeploymentService.class.getResource("/scriptRepo/" + appendScript).getFile();
                    String stringToAppend = FileUtils.readFileToString(new File(theAppend));
                    theDependencyScript += stringToAppend + "\n";
                }
                theDependencyScript += "######## End of generated script ########";
                String tmpScriptFile = scriptDir + "/" + nodeID + ".salsatmp";

                // read original script, remove the #!/bin/bash if having
                String originalScriptFile = null;
                TNodeTemplate node = ToscaStructureQuery.getNodetemplateById(nodeID, def);
                EngineLogger.logger.debug("Getting artifact template of node: {}", node.getId());
                for (TDeploymentArtifact art : node.getDeploymentArtifacts().getDeploymentArtifact()) {
                    EngineLogger.logger.debug("Checking art.Name: {}, type: {}", art.getName(), art.getArtifactType().getLocalPart());
                    if (art.getArtifactType().getLocalPart().equals("ScriptArtifactPropertiesType")) {
                        String artTemplateID = art.getArtifactRef().getLocalPart();
                        TArtifactTemplate artTemplate = ToscaStructureQuery.getArtifactTemplateById(artTemplateID, def);
                        if (artTemplate != null) {
                            originalScriptFile = artTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
                            originalScriptFile = extractedFolder + "/" + originalScriptFile;
                        }
                    }
                }
                if (originalScriptFile != null) {
                    String originalScript = FileUtils.readFileToString(new File(originalScriptFile));
                    originalScript = originalScript.replace("#!/bin/bash", "");
                    originalScript = originalScript.replace("#!/bin/sh", "");
                    theDependencyScript += originalScript;
                    FileUtils.writeStringToFile(new File(tmpScriptFile), theDependencyScript);
                    EngineLogger.logger.debug("originalScript: {}, moveto: {}", originalScriptFile, originalScriptFile + ".original");
                    FileUtils.moveFile(FileUtils.getFile(originalScriptFile), FileUtils.getFile(originalScriptFile + ".original"));
                    FileUtils.moveFile(FileUtils.getFile(tmpScriptFile), FileUtils.getFile(originalScriptFile));
                } else {
                    // TODO: there is no original script, just add new template, add tmpScript into that
                }

            }// end for each node in allRequirements analysis

            // repack the CSAR
            FileUtils.deleteQuietly(FileUtils.getFile(csarTmp));
            File directory = new File(extractedFolder);
            File[] fList = directory.listFiles();

            //CSARParser.buildCSAR(fList, csarTmp);
            String builtCSAR = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + ".csar";
            CSARParser.buildCSAR(extractedFolder, builtCSAR);

        } catch (IOException ex) {
            EngineLogger.logger.error("Error when enriching CSAR: " + csarTmp, ex);
            return "Error";
        } catch (JAXBException ex) {
            EngineLogger.logger.error("Cannot parse the Tosca definition in CSAR file: " + toscaFile, ex);
            return "Error";
        }

        // return the link to the CSAR
        String csarURLReturn = SalsaConfiguration.getSalsaCenterEndpoint() + "/rest/smart/CAMFTosca/enrich/CSAR/" + serviceName;
        EngineLogger.logger.info("Enrich CSAR done. URL to download is: {}", csarURLReturn);
        return csarURLReturn;
    }

    @GET
    @Path("/CAMFTosca/enrich/CSAR/{serviceName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response get_enriched_CSAR(@PathParam("serviceName") String serviceName) {
        EngineLogger.logger.debug("GETTING A CSAR !!!!!!!!!!!!!");
        String fileName = SalsaConfiguration.getToscaTemplateStorage() + "/" + serviceName + ".csar";
        EngineLogger.logger.debug("Read and return the CSAR file: {}. ServiceName is: {}", fileName, serviceName);
        File file = new File(fileName);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") //optional
                .build();
    }

    private String removeQuote(String quoteString) {
        return quoteString.replace("\"", "").trim();
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
                EngineLogger.logger.error("The original Tosca is not found to copy. Cannot enrich.", ex);
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
                EngineLogger.logger.error("Failure in enriching CAMF TOSCA.", ex);
            }
        }

        try {
            return FileUtils.readFileToString(new File(file));
        } catch (IOException ex) {
            EngineLogger.logger.error("Cannot read the enriched file. Big problem !", ex);
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
            EngineLogger.logger.error("Cannot marshall the multilevel requirements that you submitted !", ex);
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
                EngineLogger.logger.debug("Found policies of node: " + node.getId());
                List<TPolicy> policies = node.getPolicies().getPolicy();
                MultiLevelRequirements suReq = new MultiLevelRequirements(MonitoredElement.MonitoredElementLevel.SERVICE_UNIT);
                suReq.setName(node.getName());
                topoReq.addMultiLevelRequirements(suReq);
                Requirements reqs = new Requirements();
                reqs.setName(node.getName() + ".IaaS");
                suReq.addRequirements(reqs);
                for (TPolicy p : policies) {
                    EngineLogger.logger.debug("Analyzing policy name: {}, type:{} ", p.getName(), p.getPolicyType().toString());
                    if (p.getPolicyType().getLocalPart().equals("Requirement") && p.getPolicyType().getPrefix().equals("SmartDeployment")) {
                        String theReq = p.getName();
                        EngineLogger.logger.debug("Parsing the requirement: " + theReq);
                        // E>G> CONSTRAINT  Memory&amp;gt;2048
                        String keyword = theReq.split(" ", 2)[0].trim();
                        EngineLogger.logger.debug("The first word: {}", keyword);

                        if (keyword.equals("CONSTRAINT")) {
                            EngineLogger.logger.debug("Parsing SYBL constraint: {}" + theReq);
                            Requirement req = syblPolicyToQuelleRequirement(theReq, node, p);
                            topoReq.addRequirement(req);
                            reqs.addRequirement(req);
                        } else {
                            EngineLogger.logger.debug("Parsing CAMF constraint: {}" + theReq);
                            List<Requirement> req = CAMFPolicyToQuelleRequirement(theReq, node, p);
                            for (Requirement r : req) {
                                EngineLogger.logger.debug("Adding requirement {}", r.toString());
                                topoReq.addRequirement(r);
                                reqs.addRequirement(r);
                            }
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

    private Requirement syblPolicyToQuelleRequirement(String theReq, TNodeTemplate node, TPolicy p) {
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
        return req;
    }

    private List<Requirement> CAMFPolicyToQuelleRequirement(String theReq, TNodeTemplate node, TPolicy p) {
        List<Requirement> listReq = new ArrayList<>();
        // extract CARL Strings
        CharStream stream = new ANTLRInputStream(theReq);
        CARLLexer lexer = new CARLLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CARLParser parser = new CARLParser(tokens);
        RequirementsContext requirementsContext = parser.requirements();

        ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
        CARLProgramListener extractor = new CARLProgramListener(parser);
        walker.walk(extractor, requirementsContext); // initiate walk of tree with listener    
        org.eclipse.camf.carl.model.Requirements requirements = extractor.getRequirements();

        HashMap<String, String> allReqsOfNode = new HashMap<>();

        // os=Ubuntu; os:ver=12.04; sw=jre:1.7 ==> os=Ubuntu, 
        // here flat all the requirement of the node
        for (IRequirement req : requirements.getRequirements()) {
            EngineLogger.logger.debug("Irequirement: " + req.toString());

            // QUELLE only cares about system requirements
            if (req.getCategory().equals(RequirementCategory.SYSTEM)) {
                SystemRequirement sys = (SystemRequirement) req;
                RangeAttribute range = sys.getRange();
                String metric;
                if (sys instanceof CPURequirement) {
                    metric = "VCPU";
                } else if (sys instanceof DiskRequirement) {
                    metric = "StorageDisks";
                } else if (sys instanceof MemoryRequirement) {
                    metric = "Memory";
                } else if (sys instanceof NetworkRequirement) {
                    metric = "NetworkPerformance";
                } else {
                    metric = "unknown";
                }
                EngineLogger.logger.debug("Parsing CAMF, mapping to metric: {}", metric);
                Condition.Type type;
                if (range != null) {
                    int fromRance = range.from();
                    int toRange = range.to();
                    EngineLogger.logger.debug("We are parsing a range value: [{} --> {}]", fromRance, toRange);
                    Requirement quellReq = new Requirement(node.getName() + "_req_" + metric);
                    quellReq.addTargetServiceID(node.getId());
                    EngineLogger.logger.debug("Before setting metric. Metric: " + quellReq.getMetric());
                    quellReq.setMetric(new Metric(metric));
                    EngineLogger.logger.debug("After setting metric. Metric: " + quellReq.getMetric());
                    quellReq.addCondition(new Condition(Condition.Type.GREATER_EQUAL, null, new MetricValue(fromRance)));
                    quellReq.addCondition(new Condition(Condition.Type.LESS_EQUAL, null, new MetricValue(toRange)));
                    listReq.add(quellReq);
                } else {
                    EngineLogger.logger.debug("We are parsing equal, there is no range value !");
                    switch (sys.getOperator()) {
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
                    Requirement quellReq = new Requirement(node.getName() + "_req_" + metric);
                    quellReq.addTargetServiceID(node.getId());
                    EngineLogger.logger.debug("Before setting metric. Metric: " + quellReq.getMetric());
                    quellReq.setMetric(new Metric(metric));
                    EngineLogger.logger.debug("Before setting metric. Metric: " + quellReq.getMetric());
                    quellReq.addCondition(new Condition(type, null, new MetricValue(sys.getValue())));
                    listReq.add(quellReq);
                }

            }
        }

        return listReq;
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
