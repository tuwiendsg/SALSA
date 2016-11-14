/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.enabler;

import at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage salsa tasks. This is memory-based, no persistent yet. When salsa is
 * restarted, tasks will be lost
 *
 * @author hungld
 */
public class OrchestrationManager {

    static Logger logger = LoggerFactory.getLogger("salsa");

    // map between taskID and CloudService UUID
    static Map<Integer, String> taskCloudServiceMap = new HashMap<>();
    static int taskIdCount = 0;

    public static String getCloudServiceIdOfTask(Integer taskId) {
        return taskCloudServiceMap.get(taskId);
    }

    public static Integer startDeployment(CloudService service, OrchestrationProcess process) {
        taskIdCount = taskIdCount + 1;
        taskCloudServiceMap.put(taskIdCount, service.getUuid());
        MyThread thread = new MyThread(process, service);
        thread.start();
        return taskIdCount;
    }

    public static class MyThread extends Thread {

        OrchestrationProcess process;
        CloudService service;

        public MyThread(OrchestrationProcess process, CloudService service) {
            logger.debug("Start new orchestation process. Class: " + process.getClass().getName());
            this.process = process;
            this.service = service;
        }

        public void run() {
            process.deployCloudservice(service);
        }
    }
}
