
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The PublishTo class defines to whom a resource should be published.</p><p>Resources may be published to customers, billing entities, or the platform as a whole. Only the Master Billing Entity's admin users may publish to the platform as a whole.</p>
 * 
 * <p>Java class for publishTo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="publishTo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exclude" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="resourceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceType" type="{http://extility.flexiant.net}resourceType" minOccurs="0"/>
 *         &lt;element name="resourceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publishTo", propOrder = {
    "exclude",
    "resourceName",
    "resourceType",
    "resourceUUID"
})
public class PublishTo {

    protected boolean exclude;
    protected String resourceName;
    protected ResourceType resourceType;
    protected String resourceUUID;

    /**
     * Gets the value of the exclude property.
     * 
     */
    public boolean isExclude() {
        return exclude;
    }

    /**
     * Sets the value of the exclude property.
     * 
     */
    public void setExclude(boolean value) {
        this.exclude = value;
    }

    /**
     * Gets the value of the resourceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the value of the resourceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceName(String value) {
        this.resourceName = value;
    }

    /**
     * Gets the value of the resourceType property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceType }
     *     
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Sets the value of the resourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceType }
     *     
     */
    public void setResourceType(ResourceType value) {
        this.resourceType = value;
    }

    /**
     * Gets the value of the resourceUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceUUID() {
        return resourceUUID;
    }

    /**
     * Sets the value of the resourceUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceUUID(String value) {
        this.resourceUUID = value;
    }

}
