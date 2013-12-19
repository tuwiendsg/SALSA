package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaDeploymentTargets;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "DeploymentTarget")
@Deprecated
public class SalsaDeploymentTargetProperties extends SalsaInstanceDescriptionFuzzy {
	
	@XmlAttribute(name = "target")
	SalsaDeploymentTargets target;
	
	@XmlAttribute(name = "fuzzy")
	boolean fuzzy;
	
	public SalsaDeploymentTargetProperties(){
		this.target = SalsaDeploymentTargets.VirtualMachine;	// default
		this.fuzzy = false;
	}	

	public SalsaDeploymentTargets getTarget() {
		return target;
	}

	public void setTarget(SalsaDeploymentTargets target) {
		this.target = target;
	}

	public boolean isFuzzy() {
		return fuzzy;
	}

	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}
	
	
}
