package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.SalsaEntity.Actions.Action;
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
	
	@XmlElement(name = "actions")
	Actions actions = null;
	
	
	
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "actions")
	public static class Actions {
		@XmlElement(name = "action")
		List<Action> actions = new ArrayList<>();
		
		public static class Action{
			@XmlAttribute
			protected String name;
			@XmlElement	
	        protected String command;
			
			public String getName() {
				return name;
			}
			
			public String getCommand() {
				return command;
			}
		}
		
	}
	
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

	public List<Action> getActions() {
		if (this.actions==null){
			this.actions = new Actions();
		}
		return actions.actions;
	}
	
	public String getAction(String name){		
		if (this.actions==null){
			return null;
		}
		for (Action action : actions.actions) {
			if (action.getName().equals(name)){
				return action.getCommand();
			}
		}
		return null;
	}
	
	public void addAction(String name, String cmd){
		if (this.actions==null){
			this.actions = new Actions();
		}
		Action action = new Action();
		action.name = name;
		action.command = cmd;
		this.actions.actions.add(action);
	}

	public void addAction(Action action){
		if (this.actions==null){
			this.actions = new Actions();
		}
		Action ac = new Action();
		ac.name = action.getName();
		ac.command = action.getCommand();
		this.actions.actions.add(ac);
	}
	
	public void removeAction(Action action){
		if (this.actions==null){
			return;
		}
		this.actions.actions.remove(action);
	}
	
	
	
	
}
