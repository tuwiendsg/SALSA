package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ServiceUnit")
@XmlSeeAlso({  
    ServiceTopology.class,
    ServiceInstance.class
})
public class ServiceUnit extends SalsaEntity {	
	
	@XmlElement(name = "Replica")
	List<ServiceInstance> repLst = new CopyOnWriteArrayList<ServiceInstance>();
		
	@XmlAttribute(name = "type")
	String type;
	
	@XmlAttribute(name = "artifactType")
	String artifactType;
	
	@XmlAttribute(name = "hostedId")
	String hostedId="";
	
	@XmlAttribute(name = "connecttoId")
	List<String> connecttoId=new ArrayList<>();
		
	@XmlAttribute(name = "idCounter")
	int idCounter=0;
	
	@XmlAttribute(name = "min")
	int min=1;
	
	@XmlAttribute(name = "max")
	int max=1;
	
	@XmlAttribute(name = "artifactURL")
	String artifactURL;
	
	
	public void addInstance(ServiceInstance instance){
		repLst.add(instance);
	}	
	
	public ServiceInstance getInstanceById(int instance){		
		for (ServiceInstance node : repLst) {
			if (node.getInstanceId() == instance){
				return node;
			}			
		}
		return null;
	}
	
	public ServiceUnit(String id, String type){
		this.id = id;
		this.type = type;
	}
	
	public ServiceUnit(){}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getInstanceNumber(){
		return repLst.size();
	}
		
	public int getIdCounter() {
		return idCounter;
	}

	public void setIdCounter(int idCounter) {
		this.idCounter = idCounter;
	}
	
	public String getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}

	public String getHostedId() {
		return hostedId;
	}

	public void setHostedId(String hostedId) {
		this.hostedId = hostedId;
	}

	public List<String> getConnecttoId() {
		return connecttoId;
	}

	public void setConnecttoId(List<String> connecttoId) {
		this.connecttoId = connecttoId;
	}
	
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
	
	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
	public String getArtifactURL() {
		return artifactURL;
	}

	public void setArtifactURL(String artifactURL) {
		this.artifactURL = artifactURL;
	}

	public List<ServiceInstance> getInstanceByState(SalsaEntityState state){
		List<ServiceInstance> lst = new ArrayList<>();
		for (ServiceInstance rep : repLst) {
			if (rep.getState() == state ){
				lst.add(rep);
			}
		}
		return lst;
	}
	
	
	public int getInstanceNumberByState(SalsaEntityState state){
		int counter = 0;
		for (ServiceInstance rep : repLst) {
			if (rep.getState() == state ){
				counter++;				
			}
		}
		return counter;
	}

	public List<ServiceInstance> getInstancesList() {
		return repLst;
	}
	
	public List<ServiceInstance> getInstanceHostOn(int instanceIdOfHoster){
		List<ServiceInstance> newLst = new ArrayList<>();
		for (ServiceInstance instance : repLst) {
			if (instance.getHostedId_Integer()==instanceIdOfHoster){
				newLst.add(instance);
			}
		}
		return newLst;				
	}

}
