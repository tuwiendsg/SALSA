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
package at.ac.tuwien.dsg.salsa.engine.services.enabler;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Each pioneer is assigned with an ID, then some information: where it is
 * deployed.
 *
 * @author Duc-Hung Le
 */
public class PioneerManager {

    static Logger logger = LoggerFactory.getLogger("salsa");
    static Map<String, PioneerInfo> pioneerMap = new HashMap<>();

    public static void addPioneer(String pioneerUUID, PioneerInfo theInfo) {
        logger.debug("Adding an pioneer: " + theInfo.getUuid() + ", IP:" + theInfo.getIp() + ", META:" + theInfo.getService() + "/" + theInfo.getTopology() + "/" + theInfo.getUnit() + "/" + theInfo.getInstance());
        // remove duplicated pioneer
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getValue().equals(theInfo)) {
                pioneerMap.remove(entry.getKey());
            }
        }
        pioneerMap.put(pioneerUUID, theInfo);
    }

    public static void removePioneer(String pioneerID) {
        PioneerInfo p = pioneerMap.remove(pioneerID);
        if (p != null) {
            logger.debug("Removed pioneer: " + p.toString());
        } else {
            logger.debug("Pioneer ID: {} is not registered to be removed.", pioneerID);
        }
    }

    public static void removePioneerOfWholeService(String userName, String serviceName) {
        logger.debug("Searching for pioneerID to remove. Current pioneers: ", describe());
        List<String> removing = new ArrayList<>();
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            logger.debug("Checking pioneer: {}/{}/{}/{} if it is in the service: {} ", ai.getUserName(), ai.getService(), ai.getUnit(), ai.getInstance(), serviceName);
            if (ai.getUserName().equals(userName) && ai.getService().equals(serviceName)) {
                removing.add(ai.getUuid());
            }
        }
        for (String r : removing) {
            pioneerMap.remove(r);
        }
    }

    public static PioneerInfo getPioneerInformation(String pioneerID) {
        return pioneerMap.get(pioneerID);
    }

    public static void removeAllPioneerInfo() {
        pioneerMap.clear();
    }

    // can return null if pioneer is not registered
    public static PioneerInfo getPioneer(String user, String service, String unit, int instance) {
        logger.debug("Searching for pioneerID, what a cumbersome job... Here is the current description: {}", describe());
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            logger.debug("Checking pioneer: {}/{}/{}/{} ", ai.getUserName(), ai.getService(), ai.getUnit(), ai.getInstance());
            logger.debug("And compare with: {}/{}/{}/{} ", user, service, unit, instance);
            if (ai.getUserName().equals(user) && ai.getService().equals(service) && ai.getUnit().equals(unit) && ai.getInstance() == instance) {
                return ai;
            }
        }
        return null;
    }

    // check hosted on relationship, so properly return a pioneer, unless the node is not available
    public static PioneerInfo getPioneerIDForNode(String user, String serviceID, String unitID, int instanceID, CloudService cloudService) {
        ServiceUnit unit = cloudService.getUnitByName(unitID);
        ServiceInstance instance = unit.getInstanceByIndex(instanceID);
        ServiceUnit hostedUnit = cloudService.getUnitByName(unit.getHostedOn());
        ServiceInstance hostedInstance = hostedUnit.getInstanceByIndex(instance.getHostedInstanceIndex());
        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                && !hostedUnit.getType().endsWith(SalsaEntityType.DOCKER.getEntityTypeString())) {
            hostedUnit = cloudService.getUnitByName(hostedUnit.getHostedOn());
            hostedInstance = hostedUnit.getInstanceByIndex(hostedInstance.getHostedInstanceIndex());
        }
        return getPioneer(user, serviceID, hostedUnit.getName(), hostedInstance.getIndex());
    }

    public static String describe() {
        String pioneers = "";
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            pioneers += "Pioneer: ID=" + ai.getUuid() + ", IP=" + ai.getIp()
                    + ", User:" + ai.getUserName()
                    + ", Instance:" + ai.getService() + "/" + ai.getTopology() + "/" + ai.getUnit() + "/" + ai.getInstance() + "; \n";
        }
        return pioneers;
    }

    public static Map<String, String> describeShort() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, PioneerInfo> entry : pioneerMap.entrySet()) {
            PioneerInfo ai = entry.getValue();
            map.put(ai.getUuid(), ai.getIp() + "," + ai.getService() + "/" + ai.getUnit() + "/" + ai.getInstance());
        }
        return map;
    }

    public static int count() {
        return pioneerMap.size();
    }

    public static Map<String, PioneerInfo> getPioneerMap() {
        return pioneerMap;
    }

    public static void setPioneerMap(Map<String, PioneerInfo> pioneerMap) {
        PioneerManager.pioneerMap = pioneerMap;
    }

}
