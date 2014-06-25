package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElasticityCapability")
public class ElasticityCapability {
	 @XmlAttribute(name = "Name")
		private String name="";
	 @XmlAttribute(name = "Script")
		private String script="";
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
