package at.ac.tuwien.dsg.cloud.salsa.knowledge.process;

import generated.oasis.tosca.Definitions;
import generated.oasis.tosca.ObjectFactory;
import generated.oasis.tosca.TCapabilityDefinition;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TDeploymentArtifacts;
import generated.oasis.tosca.TExtensibleElements;
import generated.oasis.tosca.TNodeType;
import generated.oasis.tosca.TNodeType.CapabilityDefinitions;
import generated.oasis.tosca.TNodeType.RequirementDefinitions;
import generated.oasis.tosca.TNodeTypeImplementation;
import generated.oasis.tosca.TRequirementDefinition;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;

import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;


public abstract class DeploymentObject {
	
	Map<String,String> properties = new HashMap<String, String>();
	protected List<String> requirements;
	protected List<String> capabilities;
	
	/**
	 * This method need to be implement by specific Deployment Object
	 * It will try to get the properties from the target instance to
	 * do the deployment, that why the properties of those must be matched.
	 * 
	 * @param target The target instance which this one is deployed on
	 */
	public abstract void deploy(DeploymentObject target);
	
	/**
	 * This method is for validate the deployment target if it suitable for 
	 * deploying this object.
	 * @param object The target instance to be validated
	 */
	public abstract void validate(DeploymentObject object);	
	
	/**
	 * This method must be implemented, return the type of the deployment object
	 * It will be generate to Tosca NodeType name.
	 * 
	 * @return a String of type, i.e: war, java, vm, os, etc.
	 */
	public abstract String getType();
	
	
	/**
	 * Get the location of the artifact. More concrete, it's the jar file of the class
	 * 
	 * @return The location of the package which contains this class
	 */
	public String getPackageFile(){
		return DeploymentObject.class.getProtectionDomain().getCodeSource().getLocation().toString();
	}
	
	/**
	 * Get the this class name
	 * 
	 * @return the class name
	 */
	public String getImplClass(){
		return this.getClass().getName();
	}
	
	/**
	 * Provide a list of capabilities of this artifact
	 * 
	 * @return a list of property names
	 */
	public abstract List<String> getCapability();
	
	/**
	 * Provide a list of requirements. Type may be String
	 * @return a list of property names
	 */
	public abstract List<String> getRequirement();
	
	public abstract String getDefaultDeploymentArtifact();
	
	public void putProperty(String key, String value){
		this.properties.put(key, value);
	}
	
	public String getProperty(String key){
		return this.properties.get(key);
	}
	
	protected void addRequirement(String req){
		if (this.requirements == null){
			this.requirements = new ArrayList<>();
		}
		this.requirements.add(req);
	}
	
	protected void addCapability(String capa){
		if (this.capabilities == null){
			this.capabilities = new ArrayList<>();
		}
		this.capabilities.add(capa);
	}
	
		
	/**
	 * Save the Tosca of this node type to file
	 * @param file File to save
	 * @throws IOException If unable to write out.
	 * @throws JAXBException If parsing is error.
	 */
	public void exportToXMLFile(File file) throws IOException, JAXBException {
		FileUtils.writeStringToFile(file, exportToXML());
	}
	
	/**
	 * Export this deployment object to Tosca Definitions, contains a NodeType
	 * 
	 * @return A XML string of Tosca.
	 */
	public String exportToXML() throws JAXBException, IOException {
		Definitions def = new Definitions();
		ObjectFactory of = new ObjectFactory();
		TNodeType nodeType = of.createTNodeType();
		TNodeTypeImplementation nodeImpl = of.createTNodeTypeImplementation();
		def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(nodeType);
		def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(nodeImpl);
				
		nodeType.setName(getType());
		
		List<String> lst = getCapability();
		if (lst!= null && lst.size() > 0){			
			CapabilityDefinitions capas = new CapabilityDefinitions();
			for (String str : lst) {
				TCapabilityDefinition capaDef = new TCapabilityDefinition();				
				capaDef.setName(str);
				capaDef.setCapabilityType(new QName("String"));
				capas.getCapabilityDefinition().add(capaDef);
			}
			nodeType.setCapabilityDefinitions(capas);			
		}
		
		lst = getRequirement();
		if (lst!=null && lst.size() > 0){
			RequirementDefinitions reqs = new RequirementDefinitions();
			for (String str : lst){
				TRequirementDefinition reqDef = new TRequirementDefinition();
				reqDef.setName(str);
				reqDef.setRequirementType(new QName("String"));
				reqs.getRequirementDefinition().add(reqDef);
			}
			nodeType.setRequirementDefinitions(reqs);
		}
		
		TDeploymentArtifacts arts = new TDeploymentArtifacts();
		arts.getDeploymentArtifact().add(createArtifactImpl(KnowledgeArtifactType.salsaClass, this.getImplClass() , "class"));
		arts.getDeploymentArtifact().add(createArtifactImpl(KnowledgeArtifactType.knowledgePackage, getPackageFile(), "jar"));
		arts.getDeploymentArtifact().add(createArtifactImpl(KnowledgeArtifactType.artifact, getDefaultDeploymentArtifact(), getType()));
		
		nodeImpl.setDeploymentArtifacts(arts);
		
		// export to Tosca Definitions
		JAXBContext jaxbContext = JAXBContext.newInstance(TDefinitions.class, TNodeType.class, TNodeTypeImplementation.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(def, sw);
		
		return sw.toString();
		
	}
	
	private TDeploymentArtifact createArtifactImpl(KnowledgeArtifactType type, String artifactRef, String softwareType){
		TDeploymentArtifact art = new TDeploymentArtifact();
		art.setName(type.getNameString());
		if (type==KnowledgeArtifactType.artifact){
			art.setArtifactType(new QName(softwareType));
		} else {
			art.setArtifactType(new QName(type.getTypeString()));
		}		
		art.setArtifactRef(new QName(artifactRef));		
		return art;
	}
	
	public static DeploymentObject importFromXML(String xmlFile){		
		try {
			TDefinitions def = ToscaXmlProcess.readToscaFile(xmlFile);
			List<TExtensibleElements> eles = def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
			for (TExtensibleElements e : eles) {
				if (e.getClass().equals(TNodeTypeImplementation.class)){
					TNodeTypeImplementation node = (TNodeTypeImplementation) e;
					for (TDeploymentArtifact art : node.getDeploymentArtifacts().getDeploymentArtifact()) {						
						if (art.getName().equals(KnowledgeArtifactType.salsaClass.getNameString())){
							String className=art.getArtifactRef().toString();														
							return (DeploymentObject) Class.forName(className).newInstance();
						}
					}
				}
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Some error when read Tosca Knowledge !");
		return null;
	}

	@Override
	public String toString() {
		return getType();
	}	
}
