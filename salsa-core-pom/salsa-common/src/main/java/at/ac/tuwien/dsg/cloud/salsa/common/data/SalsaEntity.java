package at.ac.tuwien.dsg.cloud.salsa.common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is abstract for CloudService, ComponentTopology and Component
 * These can be map into TOSCA object in order: Definition, ServiceTemplate, NodeTemplate 
 * @author hungld
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlSeeAlso({
    SalsaCloudServiceData.class,
    SalsaTopologyData.class,
    SalsaComponentData.class    
})
public class SalsaEntity {
	@XmlAttribute(name = "id")
	String id;
	@XmlAttribute(name = "name")
	String name;	
	@XmlAttribute(name = "state")
	SalsaEntityState state;
	
	public SalsaEntity(){		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SalsaEntityState getState() {
		return state;
	}
	public void setState(SalsaEntityState state) {
		this.state = state;
	}
	
	
}
