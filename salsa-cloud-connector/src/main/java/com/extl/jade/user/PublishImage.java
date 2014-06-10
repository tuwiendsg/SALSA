
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Input parameters for method publishImage
 * 
 * <p>Java class for publishImage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="publishImage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="imageUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="publishTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="exclude" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publishImage", propOrder = {
    "imageUUID",
    "publishTo",
    "exclude"
})
public class PublishImage {

    protected String imageUUID;
    protected String publishTo;
    protected Boolean exclude;

    /**
     * Gets the value of the imageUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageUUID() {
        return imageUUID;
    }

    /**
     * Sets the value of the imageUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageUUID(String value) {
        this.imageUUID = value;
    }

    /**
     * Gets the value of the publishTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublishTo() {
        return publishTo;
    }

    /**
     * Sets the value of the publishTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublishTo(String value) {
        this.publishTo = value;
    }

    /**
     * Gets the value of the exclude property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExclude() {
        return exclude;
    }

    /**
     * Sets the value of the exclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExclude(Boolean value) {
        this.exclude = value;
    }

}
