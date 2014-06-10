
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Output result for method getHypervisorConfig
 * 
 * <p>Java class for getHypervisorConfigResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getHypervisorConfigResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hypervisorConfig" type="{http://extility.flexiant.net}mapHolder" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getHypervisorConfigResponse", propOrder = {
    "hypervisorConfig"
})
public class GetHypervisorConfigResponse {

    protected MapHolder hypervisorConfig;

    /**
     * Gets the value of the hypervisorConfig property.
     * 
     * @return
     *     possible object is
     *     {@link MapHolder }
     *     
     */
    public MapHolder getHypervisorConfig() {
        return hypervisorConfig;
    }

    /**
     * Sets the value of the hypervisorConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link MapHolder }
     *     
     */
    public void setHypervisorConfig(MapHolder value) {
        this.hypervisorConfig = value;
    }

}
