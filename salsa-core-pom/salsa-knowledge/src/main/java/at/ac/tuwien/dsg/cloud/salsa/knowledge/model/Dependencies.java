package at.ac.tuwien.dsg.cloud.salsa.knowledge.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class Dependencies {	
	
	 @XmlElement(name = "dependency", required = true)
	 protected List<String> dependency = new ArrayList<>();
	 
	 public void add(String entity){
		 this.dependency.add(entity);
	 }

	public List<String> getDependencyList() {
		return dependency;
	}

	public void setDependency(List<String> dependency) {
		this.dependency = dependency;
	}
	 
	 
}
