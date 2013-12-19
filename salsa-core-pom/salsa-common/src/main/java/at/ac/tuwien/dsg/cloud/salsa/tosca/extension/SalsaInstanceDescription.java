package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaCloudProviders;


/**
 * 
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 * 
 * @author Le Duc Hung
 * TODO: change it for extending from ToscaVMNodeTemplatePropertiesEntend. Currently: copy attributes.
 * TODO: Unified instance type. Currently: use String.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription")
public class SalsaInstanceDescription {

	@XmlElement(name = "provider")
	private SalsaCloudProviders provider;
	
	@XmlElement(name = "baseImage")
	private String baseImage;
	
	@XmlElement(name = "instanceType")
	private String instanceType;

	@XmlElement(name = "id")
	private String instanceId;

//	@XmlAttribute(name = "replicaNumber")
//	private int replicaNumber=0; 	
	
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
	
	@XmlElement(name = "Packages")
	ToscaVMNodeTemplatePropertiesEntend.PackagesDependencies packagesDependencies;

	public SalsaInstanceDescription(){		
	}
	
	public SalsaInstanceDescription(SalsaCloudProviders provider, String instanceId){
		this.provider = provider;
		this.instanceId = instanceId;
//		this.replicaNumber = replicaNumber;
	}
	
	@Deprecated
	public SalsaInstanceDescription(int replicaNumber, String instanceId,
			String privateIp, String publicIp, String privateDNS,
			String publicDNS) {

//		this.replicaNumber = replicaNumber;
		this.instanceId = instanceId;
		this.privateIp = privateIp;
		this.publicIp = publicIp;
		this.privateDNS = privateDNS;
		this.publicDNS = publicDNS;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	

//	public int getReplicaNumber() {
//		return replicaNumber;
//	}
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
//	public void setReplicaNumber(int replicaNumber) {
//		this.replicaNumber = replicaNumber;
//	}

	


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
	
	
	
	public SalsaCloudProviders getProvider() {
		return provider;
	}

	public void setProvider(SalsaCloudProviders provider) {
		this.provider = provider;
	}
	

	public String getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(String baseImage) {
		this.baseImage = baseImage;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	
	public ToscaVMNodeTemplatePropertiesEntend.PackagesDependencies getPackagesDependencies() {
		return packagesDependencies;
	}

	public void setPackagesDependencies(
			ToscaVMNodeTemplatePropertiesEntend.PackagesDependencies packagesDependencies) {
		this.packagesDependencies = packagesDependencies;
	}

	@Override
	public String toString() {
		return "SalsaInstanceDescription [provider=" + provider
				+ ", baseImage=" + baseImage + ", instanceType=" + instanceType
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
