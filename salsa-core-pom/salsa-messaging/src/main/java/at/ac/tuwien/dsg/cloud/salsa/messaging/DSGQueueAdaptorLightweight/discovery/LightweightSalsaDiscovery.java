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
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.discovery;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.Discovery;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.discovery.ADiscovery;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class LightweightSalsaDiscovery extends ADiscovery implements Discovery {

	private Config config;
	private final String restCommand = "/salsa-engine/rest/services/tosca/{serviceId}/sybl";

	public LightweightSalsaDiscovery(Config config) {
		this.config = config;
	}

	@Override
	public String discoverHost() {
		return this.discoverHost(this.config.getServiceName());
	}

	@Override
	public String discoverHost(String serviceName) {
		try {
			URI statusUri = UriBuilder.fromPath(restCommand).build(serviceName);
			HttpGet method = new HttpGet(statusUri);
			HttpHost host = new HttpHost(this.config.getDiscoveryIp(), this.config.getDiscoveryPort());
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(host, method);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					response.getEntity().writeTo(outputStream);
					String serviceDescription = new String(outputStream.toByteArray());
					
					JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
					Unmarshaller u = a.createUnmarshaller();
					if (!serviceDescription.equalsIgnoreCase("")) {
						Object object = u.unmarshal(new StringReader(serviceDescription));
						DeploymentDescription deploymentInfo = (DeploymentDescription) object;
						
						for(DeploymentUnit unit: deploymentInfo.getDeployments()) {
							if(unit.getServiceUnitID().contains("RabbitServer")) {
								return unit.getAssociatedVM().get(0).getIp();
							}
						}
					}
				} catch (JAXBException e) {
					//todo: log
				}
			}
		} catch (IOException ex) {
			//todo: log
		}
		
		return super.discoverHost(null);
	}
}
