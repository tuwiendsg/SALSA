
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Input parameters for method getHypervisorConfig
 * 
 * <p>Java class for getHypervisorConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getHypervisorConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clusterUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getHypervisorConfig", propOrder = {
    "clusterUUID"
})
public class GetHypervisorConfig {

    protected String clusterUUID;

    /**
     * Gets the value of the clusterUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClusterUUID() {
        return clusterUUID;
    }

    /**
     * Sets the value of the clusterUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClusterUUID(String value) {
        this.clusterUUID = value;
    }

}
