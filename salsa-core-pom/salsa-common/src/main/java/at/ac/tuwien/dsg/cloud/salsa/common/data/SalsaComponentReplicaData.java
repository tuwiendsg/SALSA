package at.ac.tuwien.dsg.cloud.salsa.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.tosca.extension.ToscaCapabilityString;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Replica")
@XmlSeeAlso({  
    SalsaTopologyData.class  
})
public class SalsaComponentReplicaData extends SalsaEntity {	
	
	@XmlAttribute(name = "replica")
	int replica=0;
	
	@XmlElement(name = "Properties")
	protected SalsaComponentReplicaData.Properties properties;	
	
	@XmlElement(name = "Capability")
	protected ToscaCapabilityString capability;
	
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
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


	
	public SalsaComponentReplicaData(int replica, SalsaComponentReplicaData.Properties pros){
		this.replica = replica;
		this.properties = pros;
	}
	
	public SalsaComponentReplicaData(int replica){
		this.replica = replica;		
	}
	
	public SalsaComponentReplicaData(){}
	
	public int getReplica() {
		return replica;
	}


	public void setReplica(int replica) {
		this.replica = replica;
	}

	public SalsaComponentReplicaData.Properties getProperties() {
		return properties;
	}


	public void setProperties(SalsaComponentReplicaData.Properties properties) {
		this.properties = properties;
	}

	
}
