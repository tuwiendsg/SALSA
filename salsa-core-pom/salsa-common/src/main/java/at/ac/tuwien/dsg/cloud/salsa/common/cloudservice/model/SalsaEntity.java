package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
	
	@XmlElement(name = "ConfigurationCapabilitiesQueue")
	List<String> capaQueue = new ArrayList<>();
	
	@XmlElementWrapper(name="Primitives")
	@XmlElement(name = "Primitive")
	List<PrimitiveOperation> primitive = new ArrayList<PrimitiveOperation>();
	
		
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
	
	public PrimitiveOperation getPrimitiveByName(String name){
		for (PrimitiveOperation po : primitive) {
			if (po.name.equals(name)){
				return po;						
			}
		}
		return null;
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
	
	
	public class CDATAAdapter extends XmlAdapter<String, String> {		 
	    @Override
	    public String marshal(String v) throws Exception {
	        return "<![CDATA[" + v + "]]>";
	    }
	 
	    @Override
	    public String unmarshal(String v) throws Exception {
	        return v.trim();
	    }
	}
	
	public void queueAction(String capaName){
		capaQueue.add(capaName);
	}
	
	public void unqueueAction(){
		capaQueue.remove(0);
	}
	
	public void addPrimitiveOperation(PrimitiveOperation opp){
		this.primitive.add(opp);
	}

	public List<PrimitiveOperation> getPrimitive() {
		return primitive;
	}

	public void setPrimitive(List<PrimitiveOperation> primitive) {
		this.primitive = primitive;
	}
	
}
