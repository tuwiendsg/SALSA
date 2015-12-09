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
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * To capture single host information from Ganglia This only contains the field we need. This is not the info. model for Ganglia
 *
 * @author Duc-Hung LE
 */
@XmlRootElement(name = "HOST")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class GangliaHostInfo {

    @XmlAttribute(name="NAME")
    String NAME;

    @XmlAttribute(name="IP")
    String IP;

    @XmlElement(name = "METRIC")
    List<METRIC> METRICS = new ArrayList<>();
    
    
    public static void main(String[] args) throws Exception {
        GangliaHostInfo info = new GangliaHostInfo();
        info.IP = "10.0.0.10";
        info.NAME = "testName";
        info.getMETRICS().add(new METRIC("mem_cached", "tyty", "nono", "yoyoVal"));
        info.getMETRICS().add(new METRIC("mem_free", "tyty2", "nono4", "yoyoVal1"));
        info.getMETRICS().add(new METRIC("mem_total", "tyty3", "nono1", "yoyoVal34"));
        
        try {

		
		JAXBContext jaxbContext = JAXBContext.newInstance(GangliaHostInfo.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(info, System.out);

	      } catch (JAXBException e) {
		e.printStackTrace();
	      }

    }
    

    public static class METRIC {

        @XmlAttribute(name = "NAME")
        String NAME;

        @XmlAttribute(name = "VAL")
        String VAL;

        @XmlAttribute(name = "TYPE")
        String TYPE;

        @XmlAttribute(name = "UNITS")
        String UNITS;

        public METRIC() {
        }

        public METRIC(String NAME, String VAL, String TYPE, String UNITS) {
            this.NAME = NAME;
            this.VAL = VAL;
            this.TYPE = TYPE;
            this.UNITS = UNITS;
        }
        
        

        public String getNAME() {
            return NAME;
        }

        public String getVAL() {
            return VAL;
        }

        public String getTYPE() {
            return TYPE;
        }

        public String getUNITS() {
            return UNITS;
        }
    }

    public String getNAME() {
        return NAME;
    }

    public String getIP() {
        return IP;
    }

    public List<METRIC> getMETRICS() {
        return METRICS;
    }

}
