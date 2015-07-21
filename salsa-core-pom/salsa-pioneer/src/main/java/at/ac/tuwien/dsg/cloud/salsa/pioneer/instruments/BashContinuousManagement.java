package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;

public class BashContinuousManagement {

    static Logger logger = PioneerConfiguration.logger;
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
