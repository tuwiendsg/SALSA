
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Output result for method purchaseUnits
 * 
 * <p>Java class for purchaseUnitsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purchaseUnitsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="job" type="{http://extility.flexiant.net}resource" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purchaseUnitsResponse", propOrder = {
    "job"
})
public class PurchaseUnitsResponse {

    protected Resource job;

    /**
     * Gets the value of the job property.
     * 
     * @return
     *     possible object is
     *     {@link Resource }
     *     
     */
    public Resource getJob() {
        return job;
    }

    /**
     * Sets the value of the job property.
     * 
     * @param value
     *     allowed object is
     *     {@link Resource }
     *     
     */
    public void setJob(Resource value) {
        this.job = value;
    }

}
