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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tTopologyTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tTopologyTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="NodeTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tNodeTemplate"/>
 *         &lt;element name="RelationshipTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRelationshipTemplate"/>
 *       &lt;/choice>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTopologyTemplate", propOrder = {
    "nodeTemplateOrRelationshipTemplate"
})
//@XmlSeeAlso(generated.occi.infrastructure.compute.Compute.class)
@XmlSeeAlso(at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM.class)
public class TTopologyTemplate
    extends TExtensibleElements
{

    @XmlElements({
        @XmlElement(name = "NodeTemplate", type = TNodeTemplate.class),
        @XmlElement(name = "RelationshipTemplate", type = TRelationshipTemplate.class)
    })
    protected List<TEntityTemplate> nodeTemplateOrRelationshipTemplate;

    /**
     * Gets the value of the nodeTemplateOrRelationshipTemplate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeTemplateOrRelationshipTemplate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeTemplateOrRelationshipTemplate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TNodeTemplate }
     * {@link TRelationshipTemplate }
     * 
     * 
     */
    public List<TEntityTemplate> getNodeTemplateOrRelationshipTemplate() {
        if (nodeTemplateOrRelationshipTemplate == null) {
            nodeTemplateOrRelationshipTemplate = new CopyOnWriteArrayList<TEntityTemplate>();
        }
        return this.nodeTemplateOrRelationshipTemplate;
    }

}
