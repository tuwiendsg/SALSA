package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;

/**
 * This class is abstract for CloudService, ComponentTopology and Component
 * These can be map into TOSCA object in order: Definition, ServiceTemplate, NodeTemplate 
 * @author hungld
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlSeeAlso({
    CloudService.class,
    ServiceTopology.class,
    ServiceUnit.class    
})
public class SalsaEntity {
	@XmlAttribute(name = "id")
	String id;	
	@XmlAttribute(name = "name")
	String name;	
	@XmlAttribute(name = "state")
	SalsaEntityState state;
	@XmlElement(name = "monitoring")
	SalsaEntity.Monitoring monitoring;
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "source")
    public static class Monitoring {
		@XmlAttribute(name = "name")
		protected String source;
		
        @XmlAnyElement(lax = true)
        protected Object any;
  
        public Object getAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }
    }
	
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

	public SalsaEntity.Monitoring getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(Object monitoring) {
		this.monitoring = new Monitoring();
		this.monitoring.setAny(monitoring);		
	}
	
}
