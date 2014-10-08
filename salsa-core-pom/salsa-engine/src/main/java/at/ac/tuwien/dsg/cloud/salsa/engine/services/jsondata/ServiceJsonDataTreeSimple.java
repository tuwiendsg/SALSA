package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.util.ArrayList;
import java.util.List;

public class ServiceJsonDataTreeSimple {
	String name;
	String type;
	List<ServiceJsonDataTreeSimple> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ServiceJsonDataTreeSimple> getChildren() {
		if (children==null){
			children = new ArrayList<ServiceJsonDataTreeSimple>();
		}
		return children;
	}
	public void setChildren(List<ServiceJsonDataTreeSimple> children) {
		this.children = children;
	}
	
}
