package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;


/**
 * 
 * This class acts as a container for all the information of Salsa Virtual Machine instances
 * 
 * @author Le Duc Hung
 * TODO: Unified instance type. Currently: use String.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SalsaInstanceDescription")
public class SalsaInstanceDescription_Service {

	@XmlElement(name = "pid")
	private String pid;
	
	@XmlElement(name = "state")
	private String state;

	
	public SalsaInstanceDescription_Service(){
	}
	
	public SalsaInstanceDescription_Service(String process_id){
		this.pid = process_id;
	}		

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
	
		
	
	public void updateFromMappingProperties(SalsaMappingProperties maps){
		for (SalsaMappingProperty	map : maps.getProperties()) {
			if (map.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				this.pid = map.get("pid");
				this.state = map.get("state");							
			}
		}		
	}
	
	public Map<String,String> exportToMap(){
		Map<String,String> resMap = new HashMap<String, String>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("pid", this.pid);
		map.put("state", this.state);
		
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = (Map.Entry<String,String>) iterator.next();
			if (mapEntry.getValue()!=null){
				resMap.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}		
		return resMap;
	}

}
