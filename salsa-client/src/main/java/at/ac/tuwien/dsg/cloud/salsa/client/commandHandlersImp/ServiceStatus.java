/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import at.ac.tuwien.dsg.cloud.salsa.client.RestHandler;
import at.ac.tuwien.dsg.cloud.salsa.client.data.ServiceJsonDataTree;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import javax.ws.rs.core.MediaType;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Duc-Hung LE
 */
public class ServiceStatus implements CommandHandler {

    // instance ID: serviceID/nodeID/instanceID
    @Argument(index = 0, usage = "The ID of the service to query.", metaVar = "serviceID", required = true)
    String serviceID;

//    @Argument(index = 1, usage = "The category of the instance.", metaVar = "category", required = false)
    @Option(name = "-c", usage = "The category of the instance.", metaVar = "category", required = false, forbids = {"-t"})
    String category;

    @Option(name = "-t", usage = "The type of the instance.", metaVar = "type", required = false, forbids = {"-c"})
    String unitType;

    @Override
    public void execute() {
        String path = "/viewgenerator/cloudservice/json/compact/" + serviceID;
        String result = RestHandler.callRest(Main.getSalsaAPI(path), RestHandler.HttpVerb.GET, null, MediaType.APPLICATION_JSON, null, true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ServiceJsonDataTree tree = mapper.readValue(result, ServiceJsonDataTree.class);
            if (category == null) {
                printDataNode(tree, 0);
            } else {
                try {
                    SalsaEntityType type = null;
                    if (category != null) {
                        ServiceCategory cate = ServiceCategory.valueOf(category);
                        type = mapCategoryToSalsaType(cate);
                        printDataNodeOfType(tree, type);
                    } else if (unitType != null) {
                        type = SalsaEntityType.fromString(unitType); 
                    }
                    if (type != null) {
                        printDataNodeOfType(tree, type);
                    } else {
                        System.out.println("Cannot parse the category and type !");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown category: " + category + " and type: " + unitType);
                    System.out.println("Please use a correct category:" + Arrays.asList(ServiceCategory.values()));
                    System.out.println("Or a correct unit type:" + Arrays.asList(SalsaEntityType.values()));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void printDataNode(ServiceJsonDataTree node, int level) {
        // care about only instances, do not show what is undeployed        
        String padding = String.format("%" + (level * 3 + 1) + "s", "");
        if (node.isIsAbstract()){
            System.out.format(padding + "%s: %s \n", node.getId(), node.getNodeType());
        } else if (!node.getState().equals(SalsaEntityState.DEPLOYED.toString())) {
            System.out.format(padding + "%s: %s: %s\n", node.getId(), node.getNodeType(), node.getState());
        } else {
            System.out.format(padding + "%s: %s: %s\n", node.getId(), node.getNodeType(), node.getUuid());
        }
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (ServiceJsonDataTree child : node.getChildren()) {
                printDataNode(child, level + 1);
            }
        }
    }

    private void printDataNodeOfType(ServiceJsonDataTree node, SalsaEntityType type) {
        // care about only instances, do not show what is undeployed
        if (node.getState().equals(SalsaEntityState.UNDEPLOYED.getNodeStateString()) && !node.isIsAbstract()) {
            return;
        }
        if (node.getNodeType().toLowerCase().trim().equals(type.getEntityTypeString().toLowerCase().trim())) {
            System.out.println(node.getId() + ":" + node.getNodeType() + ":" + node.getUuid());
        }
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (ServiceJsonDataTree child : node.getChildren()) {
                printDataNodeOfType(child, type);
            }
        }
    }

    public static ServiceCategory mapOldAndNewCategory(SalsaEntityType type) {
        switch (type) {
            case ARTIFACT:
            case SOFTWARE:
            case EXECUTABLE:
                return ServiceCategory.ExecutableApp;
            case DOCKER:
                return ServiceCategory.docker;
            case TOMCAT:
                return ServiceCategory.TomcatContainer;
            case OPERATING_SYSTEM:
                return ServiceCategory.VirtualMachine;
            case SERVICE:
                return ServiceCategory.SystemService;
            case WAR:
                return ServiceCategory.JavaWebApp;
            default:
                return ServiceCategory.SystemService;
        }
    }

    public static SalsaEntityType mapCategoryToSalsaType(ServiceCategory cate) {
        switch (cate) {
            case ExecutableApp:
                return SalsaEntityType.EXECUTABLE;
            case SystemService:
                return SalsaEntityType.SOFTWARE;
            case JavaWebApp:
                return SalsaEntityType.WAR;
            case docker:
                return SalsaEntityType.DOCKER;
            case TomcatContainer:
                return SalsaEntityType.TOMCAT;
            case VirtualMachine:
                return SalsaEntityType.OPERATING_SYSTEM;
            default:
                return SalsaEntityType.SOFTWARE;
        }
    }

    @Override
    public String getCommandDescription() {
        return "Get all the list of instances";
    }

}
