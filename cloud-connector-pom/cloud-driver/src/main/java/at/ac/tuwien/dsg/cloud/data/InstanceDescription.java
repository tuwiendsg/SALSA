package at.ac.tuwien.dsg.cloud.data;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ch.usi.cloud.controller.common.naming.FQN;

/**
 * 
 * This class acts as a container for all the information
 * 
 * @author Mario Bisignani (bisignam@usi.ch)
 * 
 */

public class InstanceDescription {

	// public enum InstanceState {PENDING, RUNNING, SHUTTING_DOWN, TERMINATED,
	// STOPPING, STOPPED}

	
	private FQN replicaFQN;
	private String instanceId;
	private InetAddress privateIp;
	private InetAddress publicIp;
	private String privateDNS;
	private String publicDNS;
	private String state;

	// return new InstanceDescription(instanceID,
	// InetAddress.getByName(instance.getPrivateIpAddress()),
	// InetAddress.getByName(instance.getIpAddress()),
	// instance.getPrivateDnsName(), instance.getDnsName());

	public InstanceDescription(FQN replicaFQN, String instanceId,
			String _privateIp, String _publicIp, String privateDNS,
			String publicDNS) throws UnknownHostException {
		if (_privateIp != null && _privateIp.length() > 0) {
			this.privateIp = InetAddress.getByName(_privateIp);
		}
		if (_publicIp != null && _publicIp.length() > 0) {
			this.publicIp = InetAddress.getByName(_publicIp);
		}
		this.replicaFQN = replicaFQN;
		this.instanceId = instanceId;
		this.privateDNS = privateDNS;
		this.publicDNS = publicDNS;
		this.state = "PENDING";
	}

	public InstanceDescription(FQN replicaFQN, String instanceId,
			InetAddress privateIp, InetAddress publicIp, String privateDNS,
			String publicDNS) {

		this.replicaFQN = replicaFQN;
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

	public FQN getReplicaFQN() {
		return replicaFQN;
	}

	public void setReplicaFQN(FQN replicaFQN) {
		this.replicaFQN = replicaFQN;
	}

	@Deprecated
	public InstanceDescription(String instanceId, InetAddress privateIp,
			InetAddress publicIp, String privateDNS, String publicDNS) {

		this.instanceId = instanceId;
		this.privateIp = privateIp;
		this.publicIp = publicIp;
		this.privateDNS = privateDNS;
		this.publicDNS = publicDNS;
		// this.state = state;
	}

	// Copy constructor
	public InstanceDescription(InstanceDescription inst) {

		this.instanceId = new String(inst.instanceId);
		try {
			this.privateIp = InetAddress
					.getByName(inst.privateIp.getHostName());
			this.publicIp = InetAddress.getByName(inst.publicIp.getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.privateDNS = new String(inst.privateDNS);
		this.publicDNS = new String(inst.publicDNS);

	}

	public String getInstanceId() {
		return instanceId;
	}

	public InetAddress getPrivateIp() {
		return privateIp;
	}

	public InetAddress getPublicIp() {
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
		return "InstanceDescription [replicaFQN = " + replicaFQN
				+ ", instanceId=" + instanceId + ", privateIp=" + privateIp
				+ ", publicIp=" + publicIp + ", privateDNS=" + privateDNS
				+ ", publicDNS=" + publicDNS + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstanceDescription) {
			return replicaFQN.equals(((InstanceDescription) obj)
					.getReplicaFQN());
		}
		return false;
	}

}
