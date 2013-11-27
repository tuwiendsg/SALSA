package at.ac.tuwien.dsg.cloud.salsa.common.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Component")
@XmlSeeAlso({  
    SalsaTopologyData.class,
    SalsaComponentReplicaData.class
})
public class SalsaComponentData extends SalsaEntity {	
	
	@XmlElement(name = "replicas")
	List<SalsaComponentReplicaData> repLst = new ArrayList<SalsaComponentReplicaData>();;
		
	@XmlAttribute(name = "type")
	String type;
	
	public void addReplica(SalsaComponentReplicaData replica){
		repLst.add(replica);
	}
	
	public SalsaComponentReplicaData getReplicaById(int replica){
		for (SalsaComponentReplicaData node : repLst) {
			if (node.getReplica() == replica){
				return node;
			}			
		}
		return null;
	}
	
	public SalsaComponentData(String id, String type){
		this.id = id;
		this.type = type;
	}
	
	public SalsaComponentData(){}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
