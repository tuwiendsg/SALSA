package at.ac.tuwien.dsg.cloud.salsa.knowledge.model.architecturerefine;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "DeploymentObject")
public class DeploymentObject {
	
	@XmlElement(name = "name")
	String name;
	
	@XmlElement(name = "artifact")
	String artifact;
	
	@XmlElement(name = "instrument")
	Class<? extends InstrumentalMechanism> instrument;	
	
	
	@XmlElement(name = "dependencies")
	Dependencies dependencies;
	
	@XmlElement(name = "requiements")
	Requirements requirements;
	
	
	public String exportToXML() throws JAXBException, IOException {		
		//export to XML for the knowledge-base
		JAXBContext jaxbContext = JAXBContext.newInstance(DeploymentObject.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(this, sw);
		
		return sw.toString();		
	}
	
	
	public void addDependencies(String entity){
		if (dependencies == null){
			dependencies = new Dependencies();
		}
		dependencies.add(entity);		
	}
	
	public void addRequirement(String req){
		if (requirements == null){
			requirements = new Requirements();
		}
		requirements.add(req);
	}

	public void setInstrument(Class<? extends InstrumentalMechanism> instrument) {
		this.instrument = instrument;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getArtifact() {
		return artifact;
	}


	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}


	public Dependencies getDependencies() {
		return dependencies;
	}


	public void setDependencies(Dependencies dependencies) {
		this.dependencies = dependencies;
	}


	public Requirements getRequirements() {
		return requirements;
	}


	public void setRequirements(Requirements requirements) {
		this.requirements = requirements;
	}


	public Class<? extends InstrumentalMechanism> getInstrument() {
		return instrument;
	}


	@Override
	public String toString() {
		return name;
	}
	
	
	
}
