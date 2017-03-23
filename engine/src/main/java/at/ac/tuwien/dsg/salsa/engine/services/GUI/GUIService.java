/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.GUI;

import at.ac.tuwien.dsg.salsa.engine.services.ConfigurationService;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvent;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author hungld
 */
@ManagedBean(name = "GUIService")
@ViewScoped
//@ApplicationScoped
public class GUIService {

    static Logger logger = LoggerFactory.getLogger("salsa");

    ConfigurationService restConf = JAXRSClientFactory.create(SalsaConfiguration.getSalsaCenterEndpoint() + "/rest", ConfigurationService.class);

    String currentServiceName;

    String submitedServiceName;

    UploadedFile submitedFile;

    TreeNode treeViewRoot;
    TreeNode treeTableRoot;

    public List<PioneerInfo> getPioneers() {
        List<PioneerInfo> list = new ArrayList<>();
        list.addAll(PioneerManager.getPioneerMap().values());
        return list;
    }

    public Set<String> getCloudServiceNames() {
        String listOfServices = restConf.getServiceNames();
        Set<String> theSet = new HashSet<>();
        if (listOfServices != null && !listOfServices.isEmpty()) {
            String[] theList = listOfServices.split(",");
            for (String s : theList) {
                logger.debug("Service: " + s);
                if (!s.trim().isEmpty()) {
                    theSet.add(s);
                }
            }
        }
        return theSet;
    }

    public String getServiceParamOnURL() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        currentServiceName = params.get("service");
        return currentServiceName;
    }

    public void handleUpload() {
        logger.debug("Handling file upload...");
        if (submitedFile != null) {
            logger.debug("Got the file: " + submitedFile.getFileName());
            byte[] fileContentByte = submitedFile.getContents();
            String fileContent = new String(fileContentByte);
            logger.debug("Submited service name: {} with a tosca: {}", submitedServiceName, fileContent);
            if (submitedServiceName != null) {
                try {
                    restConf.deployServiceFromXML(fileContent, submitedServiceName);
                } catch (SalsaException ex) {
                    ex.printStackTrace();
                    logger.error("Cannot deploy service. Error: " + ex.getMessage());
                }
            } else {
                logger.error("Service name is null !");
            }

        } else {
            logger.error("File uploaded is null !");
        }
    }

    ///// GETTER/SETTER
    public String getCurrentServiceName() {
        return getServiceParamOnURL();
    }

    public void setCurrentServiceName(String currentServiceName) {
        this.currentServiceName = currentServiceName;
    }

    public String getSubmitedServiceName() {
        return submitedServiceName;
    }

    public void setSubmitedServiceName(String submitedServiceName) {
        this.submitedServiceName = submitedServiceName;
    }

    public UploadedFile getSubmitedFile() {
        return submitedFile;
    }

    public void setSubmitedFile(UploadedFile submitedFile) {
        this.submitedFile = submitedFile;
    }

    // generate view for CloudService
    public TreeNode getTreeViewRoot() {
        if (this.currentServiceName == null) {
            return null;
        }
        if (this.treeViewRoot != null) {
            return treeViewRoot;
        }
        CloudService service;
        try {
            Response res = restConf.getService(currentServiceName);
            String json = IOUtils.toString((InputStream) res.getEntity(), "UTF-8");
            logger.debug("Entity String is: " + json);
            service = CloudService.fromJson(json);
//            service = res.readEntity(CloudService.class);

            if (service == null) {
                logger.debug("Cannot read cloud service. Rest status: " + res.getStatus());
                return null;
            }
            logger.debug("Ok, service is load: " + service.getName());

            TreeNode root = new DefaultTreeNode("structure", service.getName(), null);
            for (ServiceTopology topo : service.getTopologies()) {
                TreeNode treeTopo = new DefaultTreeNode("structure", topo.getName(), root);
                logger.debug(" - " + topo.getName());
                treeTopo.setExpanded(true);
//                root.getChildren().add(treeTopo);
                for (ServiceUnit theUnit : topo.getUnits()) {
                    logger.debug(" -- " + theUnit.getName());
                    TreeNode treeUnit = new DefaultTreeNode("structure", theUnit.getName(), treeTopo);
                    treeUnit.setExpanded(true);
//                    treeTopo.getChildren().add(treeUnit);
                    if (theUnit.getInstances() != null && !theUnit.getInstances().isEmpty()) {
                        for (ServiceInstance theInstance : theUnit.getInstances()) {
                            logger.debug(" --- " + theUnit.getName() + "-" + theInstance.getIndex() + " (" + theInstance.getState().toString() + ")");
                            TreeNode ii = new DefaultTreeNode("instance", theUnit.getName() + "-" + theInstance.getIndex() + " (" + theInstance.getState().toString() + ")", treeUnit);
//                            treeUnit.getChildren().add(ii);
                        }
                    }
                }
            }

            // debug: print the root tree structure
            printTree(" -- ", root);

            this.treeViewRoot = root;
            return this.treeViewRoot;

        } catch (SalsaException | IOException ex) {
            ex.printStackTrace();
            logger.error("Cannot read cloud service from REST. Response: ");
        }
        return null;
    }

    private static void printTree(String prefix, TreeNode node) {
        logger.debug("Tree: " + node.getData().toString());
        for (TreeNode child : node.getChildren()) {
            printTree(prefix + "-", child);
        }
    }

    // generate view for CloudService
    public TreeNode getTableViewTreeRoot() {
        if (this.currentServiceName == null) {
            return null;
        }
        if (this.treeTableRoot != null) {
            return this.treeTableRoot;
        }
        CloudService service;
        try {
            Response res = restConf.getService(currentServiceName);
            String json = IOUtils.toString((InputStream) res.getEntity(), "UTF-8");
            logger.debug("Entity String is: " + json);
            service = CloudService.fromJson(json);
//            service = res.readEntity(CloudService.class);

            if (service == null) {
                logger.debug("Cannot read cloud service. Rest status: " + res.getStatus());
                return null;
            }
            logger.debug("Ok, service is load: " + service.getName());

            TreeNode root = new DefaultTreeNode(new ViewTreeNode(service.getName(), "Service", "-", "-"), null);
            for (ServiceTopology topo : service.getTopologies()) {
                TreeNode treeTopo = new DefaultTreeNode(new ViewTreeNode(topo.getName(), "Topology", "-", "-"), root);
                logger.debug(" - Node topo: {} ", topo.getName());
                treeTopo.setExpanded(true);
//                root.getChildren().add(treeTopo);
                for (ServiceUnit theUnit : topo.getUnits()) {
                    logger.debug(" -- Generate Unit: {}, hostedon: {}", theUnit.getName(), theUnit.getHostedUnitName());
                    ViewTreeNode unitNode = new ViewTreeNode(theUnit.getName(), "Unit", "-", theUnit.getHostedUnitName());
                    TreeNode treeUnit = new DefaultTreeNode(unitNode, treeTopo);
                    treeUnit.setExpanded(true);
                    if (theUnit.getInstances() != null && !theUnit.getInstances().isEmpty()) {
                        for (ServiceInstance theInstance : theUnit.getInstances()) {
                            logger.debug(" --- Generate instance: {}, hosted on: {}", theUnit.getName() + "-" + theInstance.getIndex(), theUnit.getHostedUnitName() + "-" + theInstance.getHostedInstanceIndex());
                            String hostedInstanceLabel = "";
                            if (theUnit.getHostedUnitName() == null) {
                                hostedInstanceLabel = "-";
                            } else {
                                hostedInstanceLabel = theUnit.getHostedUnitName() + "-" + theInstance.getHostedInstanceIndex();
                            }
                            ViewTreeNode instanceNode = new ViewTreeNode(theUnit.getName() + "-" + theInstance.getIndex(), "Instance", theInstance.getState().toString(), hostedInstanceLabel);
                            TreeNode treeInstance = new DefaultTreeNode(instanceNode, treeUnit);
//                            treeUnit.getChildren().add(treeInstance);
                        }
                    }
//                    treeTopo.getChildren().add(treeUnit);
                }
            }

            this.treeTableRoot = root;
            return this.treeTableRoot;

        } catch (SalsaException | IOException ex) {
            ex.printStackTrace();
            logger.error("Cannot read cloud service from REST. Response: ");
        }
        return null;
    }

    public TimelineModel getTimelineEvents() {
        logger.debug("Generating timeline model...");
        TimelineModel model = new TimelineModel();

        try {
            logger.debug("Prepare to call REST !");
            Response res = restConf.getService(currentServiceName);
            logger.debug("REST is done: " + res.getStatus());
            String json = IOUtils.toString((InputStream) res.getEntity(), "UTF-8");
            logger.debug("Entity String is: " + json);
            CloudService service = CloudService.fromJson(json);
            if (service != null) {
                logger.debug("Service to get the event: " + service.getName());
                logger.debug("Number of events: " + service.getEvents().getEvents().size());
                for (SalsaEvent event : service.getEvents().getEvents()) {
                    logger.debug("-- adding event: " + event.getName());
                    model.add(new TimelineEvent(event.getName(), event.getStart(), event.getEnd()));
                }
            }

//            ObjectMapper mapper = new ObjectMapper();
//            logger.debug("TimeLineModel: " + mapper.writeValueAsString(model));
            return model;
        } catch (SalsaException | IOException ex) {
            ex.printStackTrace();
            logger.error("Cannot generate timeline events for service: " + currentServiceName);
            return null;
        }

    }

}
