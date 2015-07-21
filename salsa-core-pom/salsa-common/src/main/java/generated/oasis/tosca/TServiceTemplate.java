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
package generated.oasis.tosca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tServiceTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tServiceTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Tags" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTags" minOccurs="0"/>
 *         &lt;element name="BoundaryDefinitions" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoundaryDefinitions" minOccurs="0"/>
 *         &lt;element name="TopologyTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTopologyTemplate"/>
 *         &lt;element name="Plans" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPlans" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="substitutableNodeType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tServiceTemplate", propOrder = {
    "tags",
    "boundaryDefinitions",
    "topologyTemplate",
    "plans"
})
public class TServiceTemplate
    extends TExtensibleElements
{

    @XmlElement(name = "Tags")
    protected TTags tags;
    @XmlElement(name = "BoundaryDefinitions")
    protected TBoundaryDefinitions boundaryDefinitions;
    @XmlElement(name = "TopologyTemplate", required = true)
    protected TTopologyTemplate topologyTemplate;
    @XmlElement(name = "Plans")
    protected TPlans plans;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;
    @XmlAttribute(name = "substitutableNodeType")
    protected QName substitutableNodeType;

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link TTags }
     *     
     */
    public TTags getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTags }
     *     
     */
    public void setTags(TTags value) {
        this.tags = value;
    }

    /**
     * Gets the value of the boundaryDefinitions property.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions }
     *     
     */
    public TBoundaryDefinitions getBoundaryDefinitions() {
        return boundaryDefinitions;
    }

    /**
     * Sets the value of the boundaryDefinitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions }
     *     
     */
    public void setBoundaryDefinitions(TBoundaryDefinitions value) {
        this.boundaryDefinitions = value;
    }

    /**
     * Gets the value of the topologyTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link TTopologyTemplate }
     *     
     */
    public TTopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    /**
     * Sets the value of the topologyTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTopologyTemplate }
     *     
     */
    public void setTopologyTemplate(TTopologyTemplate value) {
        this.topologyTemplate = value;
    }

    /**
     * Gets the value of the plans property.
     * 
     * @return
     *     possible object is
     *     {@link TPlans }
     *     
     */
    public TPlans getPlans() {
        return plans;
    }

    /**
     * Sets the value of the plans property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPlans }
     *     
     */
    public void setPlans(TPlans value) {
        this.plans = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    /**
     * Gets the value of the substitutableNodeType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getSubstitutableNodeType() {
        return substitutableNodeType;
    }

    /**
     * Sets the value of the substitutableNodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setSubstitutableNodeType(QName value) {
        this.substitutableNodeType = value;
    }

}
