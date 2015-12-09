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
package at.ac.tuwien.dsg.cloud.salsa.experiments;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.DynamicPlacementHelper;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import java.io.File;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class DynamicPlacementExperiment {

    static Logger logger = LoggerFactory.getLogger(DynamicPlacementExperiment.class);
    
    public static void main(String[] args) throws Exception {
        SalsaEngineServiceIntenal engine = JAXRSClientFactory.create("http://128.130.172.216:8080/salsa-engine/rest", SalsaEngineServiceIntenal.class);;
        SalsaCenterConnector centerCon = new SalsaCenterConnector("http://128.130.172.216:8080/salsa-engine", "/tmp", logger);

        String serviceID = "M2MDaaS";
        String nodeID = "EventProcessingUnit";
        String vmNodeID = "EventProcessingUnitVM";
        String dockerNodeID = "EventProcessingDocker";
        String outputDir = "/home/hungld/test/salsa/exp/";
        String testName = "m1small";
        String csvFile = outputDir + testName + ".csv";
        double vmCost = 0.0071;
        double VMMem = 1921068;

        int maxEP = 10;
        String csvLine = "test,numVM,numService,totalMem,avgMem,usagedPercent,cost\n";
        FileUtils.writeStringToFile(new File(csvFile), csvLine, true);

        for (int i = 0; i <= maxEP; i++) {
            Response r = engine.spawnInstance(serviceID, nodeID, 1);
            
            int newID = Integer.parseInt(r.readEntity(String.class).trim());
            System.out.println("Send deployment command, the new id is: " + newID);
            String status = "undeploy";
            while (!status.equals("deployed")) {
                sleep(8);
                Response r2 = engine.getInstanceStatus(serviceID, nodeID, newID);
                status = r2.readEntity(String.class).trim();
                System.out.println("Waiting instance " + newID + " to be deployed. Now its status is: " + status);
            }
            String ganglia = engine.getGangliaHostInfo(serviceID, nodeID, newID);
            FileUtils.writeStringToFile(new File(outputDir + testName + "." + newID), ganglia);

            // csv output: instanceID, numberOfVM, numberofDocker/service, totalmem, avg mem_usaged, cost, deploymentTime
            CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceID);
            ServiceUnit vmUnit = service.getComponentById(vmNodeID);
            ServiceUnit epUnit = service.getComponentById(nodeID);

            int numberOfVM = vmUnit.getInstanceNumber();
            double totalMem = VMMem * numberOfVM;
            double totalCost = numberOfVM*vmCost;

            // get usaged mem
            double usageMem = 0;
            for (ServiceInstance vmInst : vmUnit.getInstancesList()) {
                String tmpGangliaInfo = engine.getGangliaHostInfo(serviceID, vmNodeID, vmInst.getInstanceId());
                double mem_cached = Double.parseDouble(DynamicPlacementHelper.extractGangliaMetric("mem_cached", tmpGangliaInfo));
                double mem_free = Double.parseDouble(DynamicPlacementHelper.extractGangliaMetric("mem_free", tmpGangliaInfo));
                double mem_used = VMMem - mem_cached - mem_free;
                usageMem += mem_used;
                System.out.println("The VM instance " + vmInst.getInstanceId() + " has mem_used: " + mem_used + ". Total used now: " + usageMem+"/"+totalMem);
            }

            csvLine = + i + "," + vmUnit.getInstanceNumber() + "," + epUnit.getInstanceNumber() + "," + totalMem + "," + usageMem + "," + (usageMem/totalMem*100) + "," + totalCost + "\n";
            System.out.println("Finally, mem usage percent: " + usageMem/totalMem*100);
            
            FileUtils.writeStringToFile(new File(csvFile), csvLine, true);
            
            System.out.println("Pause !!!!... Sleeping for 5 sec...");
            sleep(5);
        }
    }

    private static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
