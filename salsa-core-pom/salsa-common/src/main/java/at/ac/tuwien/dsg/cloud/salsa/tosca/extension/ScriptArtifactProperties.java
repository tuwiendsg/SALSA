package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ScriptArtifactProperty")
public class ScriptArtifactProperties {
	@XmlElement(name = "Action")
	String action;
	
	@XmlElement(name = "ScriptLanguage")
	String scriptLanguage;
	
	@XmlElement(name = "PrimaryScript")
	String scriptFile;
	
	@XmlElement(name = "ArtifactDependency")
	protected List<ArtifactDependency> artifactDependency;
	
	
	@XmlType(name = "ArtifactDependency")
	public static class ArtifactDependency{
		@XmlElement(name = "Dependency")
		protected String dependecyFile;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getScriptLanguage() {
		return scriptLanguage;
	}


	public void setScriptLanguage(String scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}


	public String getScriptFile() {
		return scriptFile;
	}


	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}


	public List<ArtifactDependency> getArtifactDependency() {
		return artifactDependency;
	}


	public void setArtifactDependency(List<ArtifactDependency> artifactDependency) {
		this.artifactDependency = artifactDependency;
	}
	
	
}


