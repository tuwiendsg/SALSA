/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.knowledge.model.cloudinformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CloudProvider")
public class CloudProvider extends Entity {

    @XmlElement(name = "ServiceUnit", required = false)
    private List<ServiceUnit> serviceUnits;
    @XmlAttribute(name = "type", required = false)
    private String type = Type.IAAS;

    {
        serviceUnits = new ArrayList<ServiceUnit>();
    }

    public CloudProvider() {
    }

    public CloudProvider(String name) {
        super(name);
    }

    public CloudProvider(String name, String type) {
        super(name);
        this.type = type;
    }

    public List<ServiceUnit> getServiceUnits() {
        return serviceUnits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addServiceUnit(ServiceUnit u) {
        this.serviceUnits.add(u);
    }

    public void removeServiceUnit(ServiceUnit u) {
        this.serviceUnits.remove(u);
    }

    public void setServiceUnits(List<ServiceUnit> serviceUnits) {
        this.serviceUnits = serviceUnits;
    }
    
    

    @Override
    public String toString() {
        return "CloudProvider{" + "name=" + name + ", type=" + type + '}';
    }

    public interface Type {

        public static final String IAAS = "IAAS";
        public static final String PAAS = "PAAS";
    }
    
    public void exportXML(File output) throws JAXBException {
    	JAXBContext jaxbContext = JAXBContext.newInstance(CloudProvider.class);
    	Marshaller marshaller = jaxbContext.createMarshaller();
    	
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    	marshaller.marshal(this, output);
    	marshaller.marshal(this, System.out);
    }
}
