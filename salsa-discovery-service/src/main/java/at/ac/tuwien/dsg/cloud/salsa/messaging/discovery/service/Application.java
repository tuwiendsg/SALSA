/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.discovery.service;

import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery.Config;
import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery.LightweightSalsaDiscovery;
import at.ac.tuwien.dsg.cloud.utilities.messaging.discoveryHelper.DiscoveryRequest;
import at.ac.tuwien.dsg.cloud.utilities.messaging.discoveryHelper.DiscoveryResponse;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.DiscoveryService;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.util.DiscoverySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@SpringBootApplication
@RestController
public class Application implements DiscoveryService {
	private static Logger logger = LoggerFactory.getLogger(Application.class);
	private LightweightSalsaDiscovery discovery;
	
	@Autowired
	private DiscoverySettings discoverySettings;
	
	public Application() {
		Config config = new Config();
		config.setDiscoveryIp(discoverySettings.getIp())
				.setDiscoveryIp(discoverySettings.getIp())
				.setServiceName(discoverySettings.getServiceName());
		this.discovery = new LightweightSalsaDiscovery(config);
	}
	
	@Override
	@RequestMapping(value="/discover", method = RequestMethod.POST)
	public DiscoveryResponse discover(@RequestBody DiscoveryRequest request) {
		logger.trace("Discovering {}", request.getServiceName());
		String ip = this.discovery.discoverHost(request.getServiceName());
		return (new DiscoveryResponse()).setServiceIp(ip);
	}
	
	@Override
	@RequestMapping(value="/isDeployed", method = RequestMethod.GET)
	public boolean isDeployed() {
		return true;
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
