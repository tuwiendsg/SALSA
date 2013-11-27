package at.ac.tuwien.dsg.cloud.salsa.common.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "CloudService")
@XmlSeeAlso({  
    SalsaTopologyData.class  
})
public class SalsaCloudServiceData extends SalsaEntity{	
	
	@XmlElement(name = "ComponentTopology")
	List<SalsaTopologyData> componentTopology;

	public SalsaCloudServiceData(){		
	}
	
	public List<SalsaTopologyData> getComponentTopologyList() {
		return componentTopology;
	}
	
	public SalsaTopologyData getComponentTopology(String topologyId){
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
}
