package at.ac.tuwien.dsg.cloud.salsa.common.artifact;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class define the artifact type
 * @author hungld
 *
 */

public class Artifact {
	@XmlAttribute(name = "name")
	String name;
	@XmlAttribute(name = "version")
	String version;
	@XmlElement(name = "Mirror")
	List<Mirror> mirrorList = new ArrayList<>();
	
	@XmlElement(name = "Operation")
	Operations operations = new Operations();
	
	@XmlElement(name="BundleConfig")
	List<Object> bundleConfig = new ArrayList<>();	// not defined yet
	
	/**
	 * artifact format, repo name on the repo list, reference of artifact on that repo
	 */
	public static class Mirror{
		@XmlElement(name = "ArtifactFormat")
		ArtifactFormat ArtifactFormat;
		@XmlElement(name = "Repository")
		String Repository;
		@XmlElement(name = "Reference")
		String Reference;
		public ArtifactFormat getArtifactFormat() {
			return ArtifactFormat;
		}
		public String getRepository() {
			return Repository;
		}
		public String getReference() {
			return Reference;
		}
	}
	
	public static class Operations{
		@XmlElement(name="Operation")
		List<Operation> operationList = new ArrayList<>();
	}
	
	
	public static class Operation{
		@XmlAttribute
		String name;
		@XmlAttribute
		String command;
		public String getOpName() {
			return name;
		}
		public String getOpCommand() {
			return command;
		}		
	}
	
		
	public Artifact(String name){
		this.name = name;
	}
	
	public Artifact(){
	}
	
	public Artifact(String name, ArtifactFormat format, String repoName, String artRef){
		this.name = name;
		addMirror(format, repoName, artRef);		
	}
	
	
	public Mirror getFirstMirror(){
		return this.mirrorList.get(0);
	}
	
	public List<Operation> getOperationList(){
		return this.operations.operationList;
	}
	
	
	public void addMirror(ArtifactFormat format, String repoName, String artRef){
		Mirror m = new Mirror();
		m.ArtifactFormat = format;
		m.Repository = repoName;
		m.Reference = artRef;
		this.mirrorList.add(m);
	}
	
	public void addOperation(String name, String command){
		Operation op = new Operation();
		op.name = name;
		op.command = command;
		this.operations.operationList.add(op);
	}
	
	public String getOperation(String name){
		for (Operation oper : this.operations.operationList) {
			if (oper.name.equals(name)){
				return oper.command;
			}
		}
		return null;
	}
}
