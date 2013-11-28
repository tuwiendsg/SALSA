package at.ac.tuwien.dsg.cloud.salsa.tosca;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public class ToscaXmlProcess {
	/**
	 * This method is used for reading Tosca definition. Use getDefinitions() to
	 * get the object.
	 * 
	 * @param Filename
	 *            Definition file of Tosca, which contain Topology
	 * @throws JAXBException
	 * @throws IOException
	 * @return Root Definition of Tosca
	 */
	public static TDefinitions readToscaFile(String fileName)
			throws JAXBException, IOException {		
		JAXBContext context = JAXBContext.newInstance(
						TDefinitions.class,
						generated.occi.infrastructure.compute.Compute.class,
						at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaCapabilityString.class,
						at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ScriptArtifactProperties.class,
						at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaInstanceDescription.class,
						at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend.class);
						
		Unmarshaller um = context.createUnmarshaller();
		TDefinitions td = (TDefinitions) um.unmarshal(new FileReader(fileName));
		return td;
	}

	public static void writeToscaDefinitionToFile(TDefinitions def,
			String fileName) {
		System.out.println("Writing Tosca to file: "+fileName);
		try {
			File file = new File(fileName);
			JAXBContext jaxbContext = JAXBContext
					.newInstance(
							def.getClass(),
							generated.occi.infrastructure.compute.Compute.class,
							at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaCapabilityString.class,
							at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ScriptArtifactProperties.class,
							at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend.class,
							at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaInstanceDescription.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(def, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method writes any part of Tosca file into XML Normally, we use to
	 * export Topology or NodeTemplate
	 * 
	 * @param object
	 *            Part need to export
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeToscaToFile(Object toscaObject, String fileName) {
		try {

			File file = new File(fileName);
			JAXBContext jaxbContext = JAXBContext
					.newInstance(
							toscaObject.getClass(),
							generated.occi.infrastructure.compute.Compute.class,
							at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaCapabilityString.class,
							at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ScriptArtifactProperties.class,
							at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaInstanceDescription.class,
							at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			String className = toscaObject.getClass().toString();

			if (fileName.equals("System.out")) {
				jaxbMarshaller.marshal(
						new JAXBElement(
								new QName("", className.substring(className
										.lastIndexOf(".") + 2), ""), // put +2 here because Classname has from of T<Class>, eg: TNodeTemplate
								toscaObject.getClass(), toscaObject),
						System.out);
			} else {
				jaxbMarshaller.marshal(
						new JAXBElement(
								new QName("", className.substring(className
										.lastIndexOf(".") + 2), ""),
								toscaObject.getClass(), toscaObject), file);
			}
			// jaxbMarshaller.marshal(toscaObject, file);
			// jaxbMarshaller.marshal(toscaObject, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static TNodeTemplate readToscaNodeTemplate(String fileName) {
		System.out.println("Reading file " + fileName + "...");
		try {
			JAXBContext context = JAXBContext.newInstance(TNodeTemplate.class);
			Unmarshaller um = context.createUnmarshaller();
			TNodeTemplate node = (TNodeTemplate) um.unmarshal(new FileReader(
					fileName));
			return node;
		} catch (Exception e) {
			System.out.println("Error while reading Toscafile. " + e);
			return null;
		}
	}

	public static void printToscaNodeTemplateDependencies(TNodeTemplate node) {
		writeToscaToFile(node, "/tmp/toscaNode");
	}

	public TNodeTemplate searchNode(String id) {

		return new TNodeTemplate();
	}

}
