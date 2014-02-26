package at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Requirements {
	// e.g: "memory > 5G" ??
	 @XmlElement(name = "requirement", required = true)
	 protected List<String> requirement = new ArrayList<>();
	 
	 public void add(String req){
		 requirement.add(req);
	 }

	public List<String> getRequirementList() {
		return requirement;
	}

	public void setRequirement(List<String> requirement) {
		this.requirement = requirement;
	}
	 
	 
}
