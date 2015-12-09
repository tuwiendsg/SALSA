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
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.GangliaHostInfo.METRIC;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import java.io.StringReader;
import java.util.Scanner;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class DynamicPlacementHelper {

    static Logger LOGGER = LoggerFactory.getLogger("EngineLogger");

    /**
     * Main method using for higher level app to check the placement condition for a service instance
     *
     * @param threadhold
     * @param service
     * @param unitID
     * @param instanceID
     * @return
     */
    public static boolean checkMemoryUsageIsBelowThreadholdByInstanceID_NoDockerConcern(float threadhold, CloudService service, String unitID, int instanceID) {
        String ip = getIpOfInstance(service, unitID, instanceID, false);
        if (ip == null) {
            return false;
        }
        LOGGER.debug("Checking memory usage of instance: {}/{}/{}, found IP of VM: {}", service.getId(), unitID, instanceID, ip);
        return checkMemoryUsageIsBelowThreadhold(threadhold, ip);
    }
    
    private static boolean checkMemoryUsageIsBelowThreadhold(float threadhold, String ip) {
        String gangliaInfo = getVMInformationFromGanglia(ip);
        String mem_free = extractGangliaMetric("mem_free", gangliaInfo);
        String mem_cached = extractGangliaMetric("mem_cached", gangliaInfo);
        String mem_total = extractGangliaMetric("mem_total", gangliaInfo);

        LOGGER.debug("Query mem_free: {}, mem_cached: {}, mem_total: {}", mem_free, mem_cached, mem_total);
        
        double mem_freeL = Float.parseFloat(mem_free);
        double mem_cachedL = Float.parseFloat(mem_cached);
        double mem_totalL = Float.parseFloat(mem_total);
        
        LOGGER.debug("Query mem_free Float value: {}, mem_cached: {}, mem_total: {}", mem_freeL, mem_cachedL, mem_totalL);
        
        double mem_free_percent = ( mem_freeL + mem_cachedL) / mem_totalL;
        double mem_usaged_percent = 1 - mem_free_percent;
        LOGGER.debug("Calculate mem_usage_percent: {}, threadhold: {}", mem_usaged_percent, threadhold);
        return (mem_usaged_percent < threadhold);
    }

    public static String getIpOfInstance(CloudService service, String nodeID, int instanceID, boolean countingDocker) {
        LOGGER.debug("Getting the IP of the instance: {}/{}/{}. Counting docker: {}", service.getId(), nodeID, instanceID, countingDocker);

        ServiceUnit unit = service.getComponentById(nodeID);
        ServiceInstance instance = unit.getInstanceById(instanceID);
        ServiceUnit hostedUnit = unit;
        ServiceInstance hostedInstance = instance;

        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                && !(countingDocker && hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString()))) {
            hostedUnit = service.getComponentById(hostedUnit.getHostedId());
            if (hostedUnit == null) {
                LOGGER.debug(" - getingIPOfInstance: but the hosted unit is null (could not happen, so weird)");
                return null;
            }
            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            // if there is no instance is deployed
            if (hostedInstance == null) {
                LOGGER.debug(" - getingIPOfInstance: but the hosted instance is null, I should check lower stacks");
                return null;
            }
        }

        LOGGER.debug("Hosted instance: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
        return vm.getPrivateIp().trim();
    }

    public static String extractGangliaMetric(String metricName, String gangliaInfo) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(GangliaHostInfo.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader sr = new StringReader(gangliaInfo);
            GangliaHostInfo info = (GangliaHostInfo) jaxbUnmarshaller.unmarshal(sr);
            LOGGER.debug("Parsed the ganglia info done. The name is: {}, there are {} metrics. Will check metric {} now", info.getNAME(), info.getMETRICS().size(), metricName);            
            for (METRIC metric : info.getMETRICS()) {
                LOGGER.debug("Checking metric name: {} ...", metric.getNAME());
                if (metric.getNAME().equals(metricName)) {
                    return metric.getVAL();
                }
            }
        } catch (JAXBException ex) {
            LOGGER.error("Cannot parse the ganglia info to get the metric with name: {}", metricName);
            ex.printStackTrace();
        }
        LOGGER.error("There is no metric with name: {}", metricName);
        return null;
    }
    
    public static String getGangliaVMInfo(CloudService service, String nodeID, int instanceID){
        String ip = getIpOfInstance(service, nodeID, instanceID, false);
        return getVMInformationFromGanglia(ip);
    }

    private static String getVMInformationFromGanglia(String ip) {
        EngineLogger.logger.debug("Querying ganglia information and return: " + ip);
        //ProcessBuilder pb = new ProcessBuilder("/usr/bin/telnet",ip,"8649");
        EngineLogger.logger.debug("Debug ganglia 1");
//        try {
            //Process p = pb.start();
//            Process p = Runtime.getRuntime().exec("/usr/bin/telnet " + ip + " 8649");
            String gangliaXML = SystemFunctions.executeCommandGetOutput("/usr/bin/telnet " + ip + " 8649", "/tmp", "");
            EngineLogger.logger.debug("Debug ganglia 2");
            System.out.println("GANGLIA XML: "+gangliaXML);
            EngineLogger.logger.debug("Get gangliaXML, length: " + gangliaXML.length());
            StringBuilder sb = new StringBuilder();
            // get subString
            Scanner scanner = new Scanner(gangliaXML);
            boolean writing = false;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("<HOST")){
                    EngineLogger.logger.debug("Found a line starting of HOST: {}", line);
                }
                if (line.contains("<HOST") && line.contains("IP=\"" + ip + "\"")) {
                    EngineLogger.logger.debug("The IP is correct: {}", ip);
                    writing = true;
                }
                if (writing) {
                    sb.append(line);
                }
                if (writing && line.contains("</HOST>")) {
                    EngineLogger.logger.debug("Closing HOST tag ...");
                    writing = false;
                    break;
                }                
            }
            scanner.close();
            EngineLogger.logger.debug("Extract the information from ganglia is done: ");
            EngineLogger.logger.debug(sb.toString());
            return sb.toString();

//            Process p = Runtime.getRuntime().exec("/bin/netcat " + ip.trim() + " 8649");
//            EngineLogger.logger.debug("Debug ganglia 2");
//            p.waitFor();
//            EngineLogger.logger.debug("Debug ganglia 3");
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(p.getInputStream()));
//            String output = "";
//            boolean writing = false;
//
//            String line = reader.readLine();
//            EngineLogger.logger.debug("Debug ganglia 4");
//            EngineLogger.logger.debug(line);
//            //line = reader.readLine(); line = reader.readLine(); line = reader.readLine(); // 3 first line of telnet command
//            // we write out the HOST information of GANGLIA_XML
//            while (line != null) {
//                if (line.contains("<HOST") && line.contains("IP=\"" + ip + "\"")) {
//                    writing = true;
//                }
//                if (writing) {
//                    output += line;
//                }
//                if (writing && line.contains("</HOST>")) {
//                    writing = false;
//                }
//
//                line = reader.readLine();
//            }
//            EngineLogger.logger.debug(output);
//            return output;
//        } catch (IOException e) {
//            EngineLogger.logger.debug(e.toString());
//            return "<Error>Error when query monitoring information !</Error>";
//
//        } catch (InterruptedException e1) {
//            EngineLogger.logger.debug(e1.toString());
//            return "Error when execute command to query monitoring information !";
//        }
    }
}
