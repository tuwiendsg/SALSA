package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationCapability")
public class ConfigurationCapability {
	@XmlAttribute(name = "name")
	String name = "deploy";
	
	@XmlElement(name = "input")
	List<String> input = new ArrayList<>();
	@XmlElement(name = "output")	
	List<String> output = new ArrayList<>();
	
	@XmlElement(name = "Dependencies")
	List<Dependency> dependencies = new ArrayList<>();
	
	@XmlElement(name = "Mechanism")
	Mechanism mechanism = new Mechanism();
	
	@XmlElement(name = "MappingProperty")
	SalsaMappingProperty mappingProperty = new SalsaMappingProperty();
	
	@XmlElement(name = "runtimeOnly")
	boolean runtimeOnly=false;
	
	public enum DependencyType{
		horizontal, vertical, local;
	}
	
	public enum RepositoryType{
		directURL, github, none;
	}
	
	public enum ExecutionType{
		command, sh, jar, RESTful, chefSolo,	// for software
		SalsaCloudConnector;					// for IaaS Cloud
	}
	
	public enum BuildType{
		maven, make, none;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
    //@XmlType(name = "Dependency")
	public static class Dependency{
		@XmlElement	
		DependencyType type;
		@XmlElement	
		String reference;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
    //@XmlType(name = "Mechanism")
	public static class Mechanism{
		// FOR ARTIFACT
		@XmlElement	
		RepositoryType artifactRepoType = RepositoryType.none;
		@XmlElement	
		BuildType buildType = BuildType.none;
		@XmlElement	
		String retrievingREF = "";
		
		// FOR EXECUTING THE ARTIFACT
		@XmlElement	
		ExecutionType executionType = ExecutionType.command;
		@XmlElement	
		String executionREF = "/bin/date";
		
		public ExecutionType getExecutionType() {
			return executionType;
		}
		public String getExecutionRef() {
			return executionREF;
		}
		public RepositoryType getArtifactRepoType() {
			return artifactRepoType;
		}
		public BuildType getBuildType() {
			return buildType;
		}
		public String getRetrievingREF() {
			return retrievingREF;
		}
		public String getExecutionREF() {
			return executionREF;
		}
		
		public void setExecutionREF(String executionREF) {
			this.executionREF = executionREF;
		}
		public void setArtifactRepoType(RepositoryType artifactRepoType) {
			this.artifactRepoType = artifactRepoType;
		}
		public void setBuildType(BuildType buildType) {
			this.buildType = buildType;
		}
		public void setRetrievingREF(String retrievingREF) {
			this.retrievingREF = retrievingREF;
		}
		public void setExecutionType(ExecutionType executionType) {
			this.executionType = executionType;
		}
		
	}

	public String getName() {
		return name;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	
	public boolean isRuntimeOnly() {
		return runtimeOnly;
	}

	public Mechanism getMechanism() {
		return mechanism;
	}
		
	
}
