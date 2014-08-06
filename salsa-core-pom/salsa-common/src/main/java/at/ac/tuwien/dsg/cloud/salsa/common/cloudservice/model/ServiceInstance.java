package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Replica")
@XmlSeeAlso({  
    ServiceTopology.class  
})
public class ServiceInstance extends SalsaEntity {	
	
	@XmlAttribute(name = "replica")
	int instanceId=0;
	
	@XmlAttribute(name = "uuid")
	UUID uuid = UUID.randomUUID();
	
	@XmlAttribute(name = "hostedId")
	int hostedId=2147483647;	// by default, it is hosted on nothing
	
	@XmlElement(name = "Properties")
	protected ServiceInstance.Properties properties;	
	
	@XmlElement(name = "Capabilities")
	protected Capabilities capabilities;
	
//	@XmlElement(name = "actionqueue")
//	protected List<String> actionqueue = new ArrayList<>();
	
	
	
//	@XmlElement(name = "Requirements")
//	protected Capabilities requirements;
	
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Capabilities {
		@XmlElement(name = "Capability")
        protected List<SalsaCapaReqString> capability = new ArrayList<>();

		public List<SalsaCapaReqString> getCapability() {
			return capability;
		}

		public void setCapability(List<SalsaCapaReqString> capabilities) {
			this.capability = capabilities;
		}  		
    }
	
//	@XmlAccessorType(XmlAccessType.FIELD)
//    @XmlType(name = "")
//    public static class Requirements {
//		@XmlElement(name = "Requirement")
//        protected List<SalsaCapaReqString> requirement = new ArrayList<>();
//
//		public List<SalsaCapaReqString> getRequirement() {
//			return requirement;
//		}
//
//		public void setRequirement(List<SalsaCapaReqString> requirements) {
//			this.requirement = requirements;
//		}  		
//    }
	
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {  "any" })
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;
  
        public Object getAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }
    }

	public String getCapabilityValue(String id){
		if (this.capabilities != null){
			for (SalsaCapaReqString capaStr : this.capabilities.getCapability()) {
				if (capaStr.getId().equals(id)){
					return capaStr.getValue();
				}
			}
		}
		return null;
	}
	
	public ServiceInstance(int replica, ServiceInstance.Properties pros){
		this.instanceId = replica;
		this.properties = pros;
	}
	
	public ServiceInstance(int replica){
		this.instanceId = replica;		
	}
	
	public ServiceInstance(){}
	
	public int getInstanceId() {
		return instanceId;
	}


	public void setInstanceId(int instance) {
		this.instanceId = instance;
	}
	
	public int getHostedId_Integer() {
		return hostedId;
	}

	public void setHostedId_Integer(int hostedId) {
		this.hostedId = hostedId;
	}

	public ServiceInstance.Properties getProperties() {		
		return properties;
	}


	public void setProperties(ServiceInstance.Properties properties) {		
		this.properties = properties;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}
		
	public UUID getUuid() {
		return uuid;
	}
	
//	public void queueAction(String action){
//		this.actionqueue.add(action);
//	}
//	
//	public void unqueueAction(String action){
//		this.actionqueue.remove(action);
//	}
//	
//	public List<String> getActionqueue() {
//		return actionqueue;
//	}

	public String convertToXML() throws JAXBException{
		JAXBContext jaxbContext = JAXBContext // beside data.class, addition
				// classes for contents
				.newInstance(ServiceUnit.class, // e.g. when update Replica, need its capability and InstanceDes.
							 SalsaInstanceDescription_VM.class,
							 SalsaCapaReqString.class);
		Marshaller msl = jaxbContext.createMarshaller();
		msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter result = new StringWriter();
		msl.marshal(this, result);
		return result.toString();
	}
	
	
}
