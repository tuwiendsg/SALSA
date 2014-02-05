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

import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapabilityString;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Replica")
@XmlSeeAlso({  
    SalsaTopologyData.class  
})
public class SalsaComponentInstanceData extends SalsaEntity {	
	
	@XmlAttribute(name = "replica")
	int instanceId=0;
	
	@XmlElement(name = "Properties")
	protected SalsaComponentInstanceData.Properties properties;	
	
	@XmlElement(name = "Capabilities")
	protected Capabilities capabilities;		
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Capabilities {
		@XmlElement(name = "Capability")
        protected List<SalsaCapabilityString> capability = new ArrayList<>();

		public List<SalsaCapabilityString> getCapability() {
			return capability;
		}

		public void setCapability(List<SalsaCapabilityString> capability) {
			this.capability = capability;
		}  		
    }
	
	
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

	public SalsaComponentInstanceData.Properties getProperties() {
		if (properties == null){
			properties = new SalsaComponentInstanceData.Properties();
		}
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
