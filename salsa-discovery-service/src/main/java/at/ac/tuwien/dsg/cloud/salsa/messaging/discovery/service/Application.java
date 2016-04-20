/* 
 * Copyright 2015 Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.discovery.service;

import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery.LightweightSalsaDiscovery;
import at.ac.tuwien.dsg.cloud.utilities.messaging.discoveryHelper.DiscoveryRequest;
import at.ac.tuwien.dsg.cloud.utilities.messaging.discoveryHelper.DiscoveryResponse;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.DiscoveryService;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.util.DiscoverySettings;
import javax.annotation.PostConstruct;
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
	private SalsaSettings salsaSettings;

	public Application() {
	}

	@PostConstruct
	private void inti() {
		DiscoverySettings config = new DiscoverySettings();
		config.setIp(salsaSettings.getIp());
		config.setPort(salsaSettings.getPort());
		config.setServiceName(salsaSettings.getServiceName());
		this.discovery = new LightweightSalsaDiscovery(config);
	}

	@Override
	@RequestMapping(value = "/discover", method = RequestMethod.POST)
	public DiscoveryResponse discover(@RequestBody DiscoveryRequest request) {
		logger.trace("Discovering {}", request.getServiceName());
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
