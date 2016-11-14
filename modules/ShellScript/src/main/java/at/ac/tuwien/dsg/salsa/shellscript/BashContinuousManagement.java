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
package at.ac.tuwien.dsg.salsa.shellscript;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BashContinuousManagement {

    static Logger logger = LoggerFactory.getLogger("BashModuleManagement");
    static Map<String, Process> processMapping = new HashMap<>();

    static public void addInstanceProcess(String actionID, Process p) {
        logger.debug("Add a node to remove queue: " + actionID);
        processMapping.put(actionID, p);
        cleanProcessMapping();
    }
    
    static public boolean killProcessInstance(String actionID) {
        logger.debug("Attempt to stop action with ID: " + actionID);
        Process p = processMapping.get(actionID);
        if (p != null) {
            p.destroy();
            cleanProcessMapping();
            return true;
        }
        return false;
    }

    private static void cleanProcessMapping() {
        for (Map.Entry<String, Process> entry : processMapping.entrySet()) {
            try {
                entry.getValue().exitValue();
                logger.debug("A process finished. Exit value: " + entry.getValue().exitValue());

                processMapping.remove(entry.getKey());
            } catch (IllegalThreadStateException e) {
                // happen when p.exitValue about false
            }

        }
    }

}
