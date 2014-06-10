
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Input parameters for method purchaseUnits
 * 
 * <p>Java class for purchaseUnits complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purchaseUnits">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productOfferUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentMethodInstanceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="units" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="interactiveInputReturnURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dryrun" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purchaseUnits", propOrder = {
    "productOfferUUID",
    "paymentMethodInstanceUUID",
    "units",
    "interactiveInputReturnURL",
    "dryrun"
})
public class PurchaseUnits {

    protected String productOfferUUID;
    protected String paymentMethodInstanceUUID;
    protected Double units;
    protected String interactiveInputReturnURL;
    protected Boolean dryrun;

    /**
     * Gets the value of the productOfferUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductOfferUUID() {
        return productOfferUUID;
    }

    /**
     * Sets the value of the productOfferUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductOfferUUID(String value) {
        this.productOfferUUID = value;
    }

    /**
     * Gets the value of the paymentMethodInstanceUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodInstanceUUID() {
        return paymentMethodInstanceUUID;
    }

    /**
     * Sets the value of the paymentMethodInstanceUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodInstanceUUID(String value) {
        this.paymentMethodInstanceUUID = value;
    }

    /**
     * Gets the value of the units property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setUnits(Double value) {
        this.units = value;
    }

    /**
     * Gets the value of the interactiveInputReturnURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteractiveInputReturnURL() {
        return interactiveInputReturnURL;
    }

    /**
     * Sets the value of the interactiveInputReturnURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteractiveInputReturnURL(String value) {
        this.interactiveInputReturnURL = value;
    }

    /**
     * Gets the value of the dryrun property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDryrun() {
        return dryrun;
    }

    /**
     * Sets the value of the dryrun property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDryrun(Boolean value) {
        this.dryrun = value;
    }

}
