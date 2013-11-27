package at.ac.tuwien.dsg.cloud.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * This class acts as a container for all the information
 * 
 * @author Mario Bisignani (bisignam@usi.ch)
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Instance")
public class SalsaInstanceDescription {

	// public enum InstanceState {PENDING, RUNNING, SHUTTING_DOWN, TERMINATED,
	// STOPPING, STOPPED}
	@XmlAttribute(name = "replicaNumber")
	private int replicaNumber=0; 		// FQN: ROOT.Customer.Service.Vee.VeeReplica
	
	@XmlAttribute(name = "id")
	private String instanceId;	
	
	@XmlElement(name = "privateIp")
	private String privateIp;
	@XmlElement(name = "publicIP")
	private String publicIp;
	@XmlElement(name = "privateDNS")
	private String privateDNS;
	@XmlElement(name = "publicDNS")
	private String publicDNS;
	@XmlElement(name = "state")
	private String state;

	public SalsaInstanceDescription(){		
	}
	public SalsaInstanceDescription(int replicaNumber, String instanceId,
			String privateIp, String publicIp, String privateDNS,
			String publicDNS) {

		this.replicaNumber = replicaNumber;
		this.instanceId = instanceId;
		this.privateIp = privateIp;
		this.publicIp = publicIp;
		this.privateDNS = privateDNS;
		this.publicDNS = publicDNS;
		this.state = "PENDING";
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	

	public int getReplicaNumber() {
		return replicaNumber;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public void setPrivateDNS(String privateDNS) {
		this.privateDNS = privateDNS;
	}
	public void setPublicDNS(String publicDNS) {
		this.publicDNS = publicDNS;
	}
	public void setReplicaNumber(int replicaNumber) {
		this.replicaNumber = replicaNumber;
	}

	


	public String getInstanceId() {
		return instanceId;
	}

	

	public String getPrivateIp() {
		return privateIp;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public String getPrivateDNS() {
		return privateDNS;
	}

	public String getPublicDNS() {
		return publicDNS;
	}
	
	@Override
	public String toString() {
		return "SalsaInstanceDescription [replicaNumber=" + replicaNumber
				+ ", instanceId=" + instanceId + ", privateIp=" + privateIp
				+ ", publicIp=" + publicIp + ", privateDNS=" + privateDNS
				+ ", publicDNS=" + publicDNS + ", state=" + state + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SalsaInstanceDescription) {
			return instanceId.equals(((SalsaInstanceDescription) obj)
					.getInstanceId());
		}
		return false;
	}

}
