/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.discovery.service;

import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery.LightweightSalsaDiscovery;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.DiscoveryRequest;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.DiscoveryResponse;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.DiscoveryService;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.util.Config;
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

	private LightweightSalsaDiscovery discovery;
	
	public Application() {
		
		//todo: think about deplouyment: how does this property file get written?!
		//todo: read from properties
		
		Config config = new Config<>();
		config.setSalsaIp("128.130.172.215");
		config.setSalsaPort(8080);
		this.discovery = new LightweightSalsaDiscovery(config);
	}
	
	@Override
	@RequestMapping(value="/discover", method = RequestMethod.POST)
	public DiscoveryResponse discover(@RequestBody DiscoveryRequest request) {
		String ip = this.discovery.discoverHost(request.getServiceName());
		return (new DiscoveryResponse()).setServiceIp(ip);
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
