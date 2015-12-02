/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.discovery.service;

import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.util.DiscoverySettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@Configuration
public class SpringConfig {
	@ConfigurationProperties(prefix = "discovery")
	@Bean
	public DiscoverySettings discoverySettings() {
		return new DiscoverySettings();
	}
}
