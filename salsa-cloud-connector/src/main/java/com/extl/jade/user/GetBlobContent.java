
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Input parameters for method getBlobContent
 * 
 * <p>Java class for getBlobContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBlobContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blobUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBlobContent", propOrder = {
    "blobUUID"
})
public class GetBlobContent {

    protected String blobUUID;

    /**
     * Gets the value of the blobUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlobUUID() {
        return blobUUID;
    }

    /**
     * Sets the value of the blobUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlobUUID(String value) {
        this.blobUUID = value;
    }

}
