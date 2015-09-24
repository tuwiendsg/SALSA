/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public class ToscaXmlProcess {

    /*
     * Use locally. Add all Tosca+Salsa related class on this context.	
     */
    private static JAXBContext getJaxbContextForTosca() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(
                // Tosca itself
                TDefinitions.class,
                // used in Tosca with name space of "Salsa"

                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString.class,
                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM.class,
                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_SystemProcess.class,
                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker.class,
                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ScriptArtifactProperties.class,
                at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.class);
        return context;
    }

    /**
     * This method is used for reading Tosca definition.
     *
     * @param Filename Definition file of Tosca, which contain Topology
     * @throws JAXBException
     * @throws IOException
     * @return Root Definition of Tosca
     */
    public static TDefinitions readToscaFile(String fileName) throws JAXBException, IOException {
        JAXBContext context = getJaxbContextForTosca();
        Unmarshaller um = context.createUnmarshaller();
        TDefinitions td = (TDefinitions) um.unmarshal(new FileReader(fileName));
        return td;
    }

    /**
     * Read the XML and parse to Tosca object
     *
     * @param xml The XML string
     * @return	The TDefinitions object
     * @throws JAXBException
     * @throws IOException
     */
    public static TDefinitions readToscaXML(String xml) throws JAXBException, IOException {
        JAXBContext context = getJaxbContextForTosca();
        Unmarshaller um = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        return (TDefinitions) um.unmarshal(reader);
    }

    /**
     * Write Tosca definitions to file
     *
     * @param def The TDefinitions object
     * @param fileName File name to save XML
     */
    public static void writeToscaDefinitionToFile(TDefinitions def, String fileName) throws JAXBException, IOException {
        System.out.println("Writing Tosca to file: " + fileName);

        File file = new File(fileName);
        JAXBContext jaxbContext = getJaxbContextForTosca();

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(def, file);

    }

    /*
     * Read an TDefinition and parse to XML
     */
    public static String writeToscaDefinitionToXML(TDefinitions def) throws JAXBException, IOException {
        JAXBContext jaxbContext = getJaxbContextForTosca();
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(def, sw);
        return sw.toString();

    }

    // The following methods are for test only
    /**
     * This method writes any part of Tosca file into XML Normally, we use to export Topology or NodeTemplate
     *
     * @param object Part need to export
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void writeToscaElementToFile(Object toscaObject, String fileName) throws JAXBException, IOException {
        File file = new File(fileName);
        JAXBContext jaxbContext = getJaxbContextForTosca();
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String className = toscaObject.getClass().toString();

        if (fileName.equals("System.out")) {
            jaxbMarshaller.marshal(new JAXBElement(new QName("", className.substring(className.lastIndexOf(".") + 2), ""), // put +2 here because Classname has from of T<Class>, eg: TNodeTemplate
                    toscaObject.getClass(), toscaObject), System.out);
        } else {
            jaxbMarshaller.marshal(new JAXBElement(new QName("", className.substring(className.lastIndexOf(".") + 2), ""),
                    toscaObject.getClass(), toscaObject), file);
        }

    }

    public static TNodeTemplate readToscaNodeTemplate(String fileName) throws JAXBException, IOException {
        System.out.println("Reading file " + fileName + "...");

        JAXBContext context = JAXBContext.newInstance(TNodeTemplate.class);
        Unmarshaller um = context.createUnmarshaller();
        TNodeTemplate node = (TNodeTemplate) um.unmarshal(new FileReader(
                fileName));
        return node;
    }

    /**
     * Read the XML and parse to Tosca object
     *
     * @param xml The XML string
     * @return	The TDefinitions object
     * @throws JAXBException
     * @throws IOException
     */
    public static TNodeTemplate readToscaNodeTemplateFromString(String xml)
            throws JAXBException, IOException {
        JAXBContext context = getJaxbContextForTosca();
        Unmarshaller um = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        return (TNodeTemplate) um.unmarshal(reader);
    }

    public static void printToscaNodeTemplateDependencies(TNodeTemplate node) throws JAXBException, IOException {
        writeToscaElementToFile(node, "/tmp/toscaNode");
    }

    public TNodeTemplate searchNode(String id) {

        return new TNodeTemplate();
    }

}
