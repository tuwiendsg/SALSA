/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight;

import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery.LightweightSalsaDiscovery;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.util.DiscoverySettings;

/**
 *
 * @author Duc-Hung LE
 */
public class DSGQueueConnector {

	LightweightSalsaDiscovery instance;
	DiscoverySettings config = new DiscoverySettings();

	public DSGQueueConnector(String broker) {
		// configure in salsa.engine.properties like: 128.130.172.215:8080:ManualTestRabbitService:1
		// the last digit is the number of server (server count), but will not be used here !
		String[] confs = broker.split(":");
		if (confs.length < 3) {
			System.out.println("Cannot read the broker information");
			return;
		}
		try {
			config.setIp(confs[0]);
			config.setPort(Integer.parseInt(confs[1]));
			config.setServiceName(confs[2]);
			this.instance = new LightweightSalsaDiscovery(config);
		} catch (NumberFormatException e) {
			config = null;
			System.out.println("Cannot parse DSG queue configuration, it should be salsaIP:salsaPort:serviceName:NumOfInstances");
		}
	}
}
