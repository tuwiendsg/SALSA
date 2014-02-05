package at.ac.tuwien.dsg.cloud.salsa.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "CloudService")
@XmlSeeAlso({  
    SalsaTopologyData.class  
})
public class SalsaCloudServiceData extends SalsaEntity{	
	
	@XmlElement(name = "ServiceTopology")
	List<SalsaTopologyData> componentTopology;

	public SalsaCloudServiceData(){		
	}
	
	public List<SalsaTopologyData> getComponentTopologyList() {
		return componentTopology;
	}
	
	public SalsaTopologyData getComponentTopologyById(String topologyId){
		for (SalsaTopologyData topo : componentTopology) {
			if (topo.getId().equals(topologyId)){
				return topo;
			}
		}
		return null;
	}
	
	public void addComponentTopology(SalsaTopologyData topo){
		if (componentTopology==null) {
			componentTopology = new ArrayList<>();
		}
		this.componentTopology.add(topo);
	}
	
	public void removeComponentTopology(SalsaTopologyData topo){
		this.componentTopology.remove(topo);
	}
	
	public SalsaTopologyData getFirstTopology(){
		if (componentTopology.isEmpty()){
			return null;
		}
		return componentTopology.get(0);
	}
	
	
	public SalsaComponentData getComponentById(String topologyId, String nodeId){
		if (componentTopology != null){
			SalsaTopologyData topo = getComponentTopologyById(topologyId);
				if (topo != null){
					return topo.getComponentById(nodeId);
				}			
		}
		return null;
	}
	
	public SalsaComponentInstanceData getReplicaById(String topologyId, String nodeId, int replica){
		SalsaComponentData component = getComponentById(topologyId, nodeId);
		if (component != null){
			return component.getInstanceById(replica);
		}
		return null;
	}
	
	/**
	 * Get all Node of a Type. E.g: Get all VM node's.
	 * @param type
	 * @return
	 */
	public List<SalsaComponentData> getAllComponentByType(SalsaEntityType type){
		List<SalsaComponentData> comList = new ArrayList<>();
		for (SalsaTopologyData topo : componentTopology) {
			comList.addAll(topo.getComponentsByType(type));
		}
		return comList;
	}
	
	public List<SalsaComponentInstanceData> getAllReplicaByType(SalsaEntityType type){
		List<SalsaComponentInstanceData> repList = new ArrayList<>();
		List<SalsaComponentData> comList = getAllComponentByType(type);
		for (SalsaComponentData com : comList) {
			repList.addAll(com.getInstanceList());
		}		
		return repList;
	}
}
