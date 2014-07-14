package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "CloudService")
@XmlSeeAlso({  
    ServiceTopology.class  
})
public class CloudService extends SalsaEntity{	
	
	@XmlElement(name = "ServiceTopology")
	List<ServiceTopology> componentTopology;

	public CloudService(){		
	}
	
	public List<ServiceTopology> getComponentTopologyList() {
		return componentTopology;
	}
	
	public ServiceTopology getComponentTopologyById(String topologyId){
		for (ServiceTopology topo : componentTopology) {
			if (topo.getId().equals(topologyId)){
				return topo;
			}
		}
		return null;
	}
	
	public void addComponentTopology(ServiceTopology topo){
		if (componentTopology==null) {
			componentTopology = new ArrayList<>();
		}
		this.componentTopology.add(topo);
	}
	
	public void removeComponentTopology(ServiceTopology topo){
		this.componentTopology.remove(topo);
	}
	
//	public ServiceTopology getFirstTopology(){
//		if (componentTopology.isEmpty()){
//			return null;
//		}
//		return componentTopology.get(0);
//	}
	
	
	public ServiceUnit getComponentById(String topologyId, String nodeId){
		if (componentTopology != null){
			ServiceTopology topo = getComponentTopologyById(topologyId);
				if (topo != null){
					return topo.getComponentById(nodeId);
				}			
		}
		return null;
	}
	
	public ServiceUnit getComponentById(String nodeId){
		if (componentTopology != null){
			for (ServiceTopology topo : componentTopology) {
				ServiceUnit unit = getComponentById(topo.getId(), nodeId);
				if (unit!=null){
					return unit;
				}
			}
		}
		return null;
	}
	
	public ServiceInstance getInstanceById(String nodeId, int instanceId){
		ServiceUnit component = getComponentById(nodeId);
		if (component != null){
			return component.getInstanceById(instanceId);
		}
		return null;
	}
	
	public ServiceInstance getInstanceById(String topologyId, String nodeId, int instanceId){
		ServiceUnit component = getComponentById(topologyId, nodeId);
		if (component != null){
			return component.getInstanceById(instanceId);
		}
		return null;
	}
	
	/**
	 * Get all Node of a Type. E.g: Get all VM node's.
	 * @param type
	 * @return
	 */
	public List<ServiceUnit> getAllComponentByType(SalsaEntityType type){
		List<ServiceUnit> comList = new ArrayList<>();
		for (ServiceTopology topo : componentTopology) {
			comList.addAll(topo.getComponentsByType(type));
		}
		return comList;
	}
	
	public List<ServiceUnit> getAllComponent(){
		List<ServiceUnit> comList = new ArrayList<>();
		for (ServiceTopology topo : componentTopology) {
			comList.addAll(topo.getComponents());
		}
		return comList;
	}
	
	public List<ServiceInstance> getAllReplicaByType(SalsaEntityType type){
		List<ServiceInstance> repList = new ArrayList<>();
		List<ServiceUnit> comList = getAllComponentByType(type);
		for (ServiceUnit com : comList) {
			repList.addAll(com.getInstancesList());
		}		
		return repList;
	}
	
	public ServiceTopology getTopologyOfNode(String serviceUnitId){
		for (ServiceTopology topo : componentTopology) {
			for (ServiceUnit tmpUnit : topo.getComponents()) {
				if (tmpUnit.getId().equals(serviceUnitId)){
					return topo;
				}
			}
		}
		return null;
	}
}
