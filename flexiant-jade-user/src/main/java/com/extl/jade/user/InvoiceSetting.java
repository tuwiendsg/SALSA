
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A billing entity's settings for an invoice
 * 
 * <p>Java class for invoiceSetting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invoiceSetting">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decimalPlace" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="decimalSeparator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupSeparator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tax" type="{http://extility.flexiant.net}tax" minOccurs="0"/>
 *         &lt;element name="invoiceHeaders">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://extility.flexiant.net}invoiceHeader" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="pageSize" type="{http://extility.flexiant.net}pdfPageSize" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invoiceSetting", propOrder = {
    "dateFormat",
    "timeZone",
    "decimalPlace",
    "decimalSeparator",
    "groupSeparator",
    "tax",
    "invoiceHeaders",
    "pageSize"
})
public class InvoiceSetting {

    protected String dateFormat;
    protected String timeZone;
    protected int decimalPlace;
    protected String decimalSeparator;
    protected String groupSeparator;
    protected Tax tax;
    @XmlElement(required = true)
    protected InvoiceSetting.InvoiceHeaders invoiceHeaders;
    protected PdfPageSize pageSize;

    /**
     * Gets the value of the dateFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the value of the dateFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

    /**
     * Gets the value of the timeZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the value of the timeZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeZone(String value) {
        this.timeZone = value;
    }

    /**
     * Gets the value of the decimalPlace property.
     * 
     */
    public int getDecimalPlace() {
        return decimalPlace;
    }

    /**
     * Sets the value of the decimalPlace property.
     * 
     */
    public void setDecimalPlace(int value) {
        this.decimalPlace = value;
    }

    /**
     * Gets the value of the decimalSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Sets the value of the decimalSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimalSeparator(String value) {
        this.decimalSeparator = value;
    }

    /**
     * Gets the value of the groupSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupSeparator() {
        return groupSeparator;
    }

    /**
     * Sets the value of the groupSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupSeparator(String value) {
        this.groupSeparator = value;
    }

    /**
     * Gets the value of the tax property.
     * 
     * @return
     *     possible object is
     *     {@link Tax }
     *     
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tax }
     *     
     */
    public void setTax(Tax value) {
        this.tax = value;
    }

    /**
     * Gets the value of the invoiceHeaders property.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceSetting.InvoiceHeaders }
     *     
     */
    public InvoiceSetting.InvoiceHeaders getInvoiceHeaders() {
        return invoiceHeaders;
    }

    /**
     * Sets the value of the invoiceHeaders property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceSetting.InvoiceHeaders }
     *     
     */
    public void setInvoiceHeaders(InvoiceSetting.InvoiceHeaders value) {
        this.invoiceHeaders = value;
    }

    /**
     * Gets the value of the pageSize property.
     * 
     * @return
     *     possible object is
     *     {@link PdfPageSize }
     *     
     */
    public PdfPageSize getPageSize() {
        return pageSize;
    }

    /**
     * Sets the value of the pageSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link PdfPageSize }
     *     
     */
    public void setPageSize(PdfPageSize value) {
        this.pageSize = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://extility.flexiant.net}invoiceHeader" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class InvoiceHeaders {

        protected List<InvoiceSetting.InvoiceHeaders.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link InvoiceSetting.InvoiceHeaders.Entry }
         * 
         * 
         */
        public List<InvoiceSetting.InvoiceHeaders.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<InvoiceSetting.InvoiceHeaders.Entry>();
            }
            return this.entry;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://extility.flexiant.net}invoiceHeader" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected InvoiceHeader key;
            protected String value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link InvoiceHeader }
             *     
             */
            public InvoiceHeader getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link InvoiceHeader }
             *     
             */
            public void setKey(InvoiceHeader value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
