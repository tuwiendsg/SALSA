package at.ac.tuwien.dsg.cloud.salsa.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaRelationshipType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "relationship")
@XmlSeeAlso({  
    SalsaTopologyData.class    
})
public class SalsaReplicaRelationship {
	@XmlAttribute(name = "type")
	SalsaRelationshipType type;
	
	@XmlElement(name = "source")
	SalsaReplicaInstanceReference source;
	@XmlElement(name = "target")
	SalsaReplicaInstanceReference target;
	
	public SalsaReplicaRelationship(){}
	
	public SalsaReplicaRelationship(String sourceNode, int sourceRep, String targetNode, int targetRep){
		this.source = new SalsaReplicaInstanceReference(sourceNode, sourceRep);
		this.target = new SalsaReplicaInstanceReference(targetNode, targetRep);
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
	public static class SalsaReplicaInstanceReference{
		@XmlAttribute(name = "node")
		String node;
		@XmlAttribute(name = "replica")
		int replica;
		
		public SalsaReplicaInstanceReference(){}
		
		public SalsaReplicaInstanceReference(String node, int replica){
			this.node = node;
			this.replica = replica;
		}
		
		public String getNode() {
			return node;
		}
		public void setNode(String node) {
			this.node = node;
		}
		public int getReplica() {
			return replica;
		}
		public void setReplica(int replica) {
			this.replica = replica;
		}		
	}

	@Override
	public String toString() {
		return "SalsaReplicaRelationship [source=" + source.getNode()+"."+source.getReplica() + ", target="
				+ target.getNode()+"."+target.getReplica() + "]";
	}

	public SalsaRelationshipType getType() {
		return type;
	}

	public void setType(SalsaRelationshipType type) {
		this.type = type;
	}
		
}
