package at.ac.tuwien.dsg.cloud.salsa.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Replica")
@XmlSeeAlso({  
    SalsaTopologyData.class  
})
public class SalsaComponentInstanceData extends SalsaEntity {	
	
	@XmlAttribute(name = "replica")
	int instanceId=0;
	
	@XmlAttribute(name = "hostedId")
	int hostedId=0;
	
	@XmlElement(name = "Properties")
	protected SalsaComponentInstanceData.Properties properties;	
	
	@XmlElement(name = "Capabilities")
	protected Capabilities capabilities;
	
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
	
	public SalsaComponentInstanceData(int replica, SalsaComponentInstanceData.Properties pros){
		this.instanceId = replica;
		this.properties = pros;
	}
	
	public SalsaComponentInstanceData(int replica){
		this.instanceId = replica;		
	}
	
	public SalsaComponentInstanceData(){}
	
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

	public SalsaComponentInstanceData.Properties getProperties() {		
		return properties;
	}


	public void setProperties(SalsaComponentInstanceData.Properties properties) {		
		this.properties = properties;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}
	
}
