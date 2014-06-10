
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Input parameters for method fetchDisk
 * 
 * <p>Java class for fetchDisk complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fetchDisk">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="skeletonDisk" type="{http://extility.flexiant.net}disk" minOccurs="0"/>
 *         &lt;element name="fetchParameters" type="{http://extility.flexiant.net}fetchParameters" minOccurs="0"/>
 *         &lt;element name="when" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fetchDisk", propOrder = {
    "skeletonDisk",
    "fetchParameters",
    "when"
})
public class FetchDisk {

    protected Disk skeletonDisk;
    protected FetchParameters fetchParameters;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar when;

    /**
     * Gets the value of the skeletonDisk property.
     * 
     * @return
     *     possible object is
     *     {@link Disk }
     *     
     */
    public Disk getSkeletonDisk() {
        return skeletonDisk;
    }

    /**
     * Sets the value of the skeletonDisk property.
     * 
     * @param value
     *     allowed object is
     *     {@link Disk }
     *     
     */
    public void setSkeletonDisk(Disk value) {
        this.skeletonDisk = value;
    }

    /**
     * Gets the value of the fetchParameters property.
     * 
     * @return
     *     possible object is
     *     {@link FetchParameters }
     *     
     */
    public FetchParameters getFetchParameters() {
        return fetchParameters;
    }

    /**
     * Sets the value of the fetchParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link FetchParameters }
     *     
     */
    public void setFetchParameters(FetchParameters value) {
        this.fetchParameters = value;
    }

    /**
     * Gets the value of the when property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getWhen() {
        return when;
    }

    /**
     * Sets the value of the when property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setWhen(XMLGregorianCalendar value) {
        this.when = value;
    }

}
