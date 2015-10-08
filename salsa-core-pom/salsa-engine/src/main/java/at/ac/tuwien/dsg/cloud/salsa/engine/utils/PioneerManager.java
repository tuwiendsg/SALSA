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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.PioneerInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each pioneer is assigned with an ID, then some information: where it is deployed.
 *
 * @author Duc-Hung Le
 */
public class PioneerManager {

    static Map<String, PioneerInfo> pioneerMap = new HashMap<>();

    public static void addPioneer(String pioneerID, PioneerInfo fullInstanceID) {
        EngineLogger.logger.debug("Adding an pioneer: " + fullInstanceID.getId() + ", IP:" + fullInstanceID.getIp() + ", META:" + fullInstanceID.getService() + "/" + fullInstanceID.getTopology() + "/" + fullInstanceID.getUnit() + "/" + fullInstanceID.getInstance());
        // remove duplicated pioneer
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getValue().equals(fullInstanceID)){
                pioneerMap.remove(entry.getKey());
            }
        }
        pioneerMap.put(pioneerID, fullInstanceID);
    }

    public static void removePioneer(String pioneerID) {
        PioneerInfo p = pioneerMap.remove(pioneerID);
        if (p != null) {
            EngineLogger.logger.debug("Removed pioneer: " + p.toString());
        } else {
            EngineLogger.logger.debug("Pioneer ID: {} is not registered to be removed.", pioneerID);
        }
    }

    public static void removePioneerOfWholeService(String userName, String serviceName) {
        EngineLogger.logger.debug("Searching for pioneerID to remove. Current pioneers: ", describe());
        List<String> removing = new ArrayList<>();
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            EngineLogger.logger.debug("Checking pioneer: {}/{}/{}/{} if it is in the service: {} ", ai.getUserName(), ai.getService(), ai.getUnit(), ai.getInstance(), serviceName);
            if (ai.getUserName().equals(userName) && ai.getService().equals(serviceName)) {
                removing.add(ai.getId());
            }
        }
        for (String r : removing) {
            pioneerMap.remove(r);
        }
    }

    public static PioneerInfo getPioneerInformation(String pioneerID) {
        return pioneerMap.get(pioneerID);
    }
    
    public static void removeAllPioneerInfo(){
        pioneerMap.clear();
    }

    // can return null
    public static String getPioneerID(String user, String service, String unit, int instance) {
        EngineLogger.logger.debug("Searching for pioneerID, what a cumbersome job... Here is the current description: {}", describe());
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            EngineLogger.logger.debug("Checking pioneer: {}/{}/{}/{} ", ai.getUserName(), ai.getService(), ai.getUnit(), ai.getInstance());
            EngineLogger.logger.debug("And compare with: {}/{}/{}/{} ", user, service, unit, instance);
            if (ai.getUserName().equals(user) && ai.getService().equals(service) && ai.getUnit().equals(unit) && ai.getInstance() == instance) {
                return entry.getKey();
            }
        }
        return null;
    }

    // check hosted on relationship, so properly return a pioneer, unless the node is not available
    public static String getPioneerIDForNode(String user, String serviceID, String unitID, int instanceID, CloudService cloudService) {
        ServiceUnit unit = cloudService.getComponentById(unitID);
        ServiceInstance instance = unit.getInstanceById(instanceID);
        ServiceUnit hostedUnit = cloudService.getComponentById(unit.getHostedId());
        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                && !hostedUnit.getType().endsWith(SalsaEntityType.DOCKER.getEntityTypeString())) {
            hostedUnit = cloudService.getComponentById(hostedUnit.getHostedId());
            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
        }
        return getPioneerID(user, serviceID, hostedUnit.getId(), hostedInstance.getInstanceId());
    }

    public static String describe() {
        String pioneers = "";
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            pioneers += "Pioneer: ID=" + ai.getId() + ", IP=" + ai.getIp()
                    + ", User:" + ai.getUserName()
                    + ", Instance:" + ai.getService() + "/" + ai.getTopology() + "/" + ai.getUnit() + "/" + ai.getInstance() + "; \n";
        }
        return pioneers;
    }

    public static Map<String, String> describeShort() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            map.put(ai.getId(), ai.getIp() + "," + ai.getService() + "/" + ai.getUnit() + "/" + ai.getInstance());
        }
        return map;
    }

    public static int count() {
        return pioneerMap.size();
    }
}
