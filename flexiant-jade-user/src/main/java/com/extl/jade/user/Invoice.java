
package com.extl.jade.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The Invoice class holds the base data for each invoice.</p>
 * 
 * <p>Java class for invoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invoice">
 *   &lt;complexContent>
 *     &lt;extension base="{http://extility.flexiant.net}resource">
 *       &lt;sequence>
 *         &lt;element name="invoiceTotalInc" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="invoiceDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="invoiceTaxAmt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="invoiceTotalExc" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="paidDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="invoiceNo" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="invoiceItems" type="{http://extility.flexiant.net}invoiceItem" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="testMode" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="customerAddress" type="{http://extility.flexiant.net}address" minOccurs="0"/>
 *         &lt;element name="billingAddress" type="{http://extility.flexiant.net}address" minOccurs="0"/>
 *         &lt;element name="customerVatNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="billingEntityVatNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxRate" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="pdfRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentMethodInstanceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://extility.flexiant.net}invoiceStatus" minOccurs="0"/>
 *         &lt;element name="dueDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cutOffDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="paymentMethodInstanceUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currencyId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="customerUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creditNote" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="transactionUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invoice", propOrder = {
    "invoiceTotalInc",
    "invoiceDate",
    "invoiceTaxAmt",
    "invoiceTotalExc",
    "paidDate",
    "invoiceNo",
    "invoiceItems",
    "customerName",
    "testMode",
    "customerAddress",
    "billingAddress",
    "customerVatNo",
    "billingEntityVatNo",
    "taxRate",
    "pdfRef",
    "paymentMethodInstanceName",
    "status",
    "dueDate",
    "cutOffDate",
    "paymentMethodInstanceUUID",
    "currencyId",
    "customerUUID",
    "creditNote",
    "transactionUUID",
    "pdf"
})
public class Invoice
    extends Resource
{

    protected double invoiceTotalInc;
    protected Long invoiceDate;
    protected double invoiceTaxAmt;
    protected double invoiceTotalExc;
    protected Long paidDate;
    protected long invoiceNo;
    @XmlElement(nillable = true)
    protected List<InvoiceItem> invoiceItems;
    protected String customerName;
    protected boolean testMode;
    protected Address customerAddress;
    protected Address billingAddress;
    protected String customerVatNo;
    protected String billingEntityVatNo;
    protected double taxRate;
    protected String pdfRef;
    protected String paymentMethodInstanceName;
    protected InvoiceStatus status;
    protected Long dueDate;
    protected Long cutOffDate;
    protected String paymentMethodInstanceUUID;
    protected long currencyId;
    protected String customerUUID;
    protected boolean creditNote;
    protected String transactionUUID;
    protected String pdf;

    /**
     * Gets the value of the invoiceTotalInc property.
     * 
     */
    public double getInvoiceTotalInc() {
        return invoiceTotalInc;
    }

    /**
     * Sets the value of the invoiceTotalInc property.
     * 
     */
    public void setInvoiceTotalInc(double value) {
        this.invoiceTotalInc = value;
    }

    /**
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setInvoiceDate(Long value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the invoiceTaxAmt property.
     * 
     */
    public double getInvoiceTaxAmt() {
        return invoiceTaxAmt;
    }

    /**
     * Sets the value of the invoiceTaxAmt property.
     * 
     */
    public void setInvoiceTaxAmt(double value) {
        this.invoiceTaxAmt = value;
    }

    /**
     * Gets the value of the invoiceTotalExc property.
     * 
     */
    public double getInvoiceTotalExc() {
        return invoiceTotalExc;
    }

    /**
     * Sets the value of the invoiceTotalExc property.
     * 
     */
    public void setInvoiceTotalExc(double value) {
        this.invoiceTotalExc = value;
    }

    /**
     * Gets the value of the paidDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPaidDate() {
        return paidDate;
    }

    /**
     * Sets the value of the paidDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPaidDate(Long value) {
        this.paidDate = value;
    }

    /**
     * Gets the value of the invoiceNo property.
     * 
     */
    public long getInvoiceNo() {
        return invoiceNo;
    }

    /**
     * Sets the value of the invoiceNo property.
     * 
     */
    public void setInvoiceNo(long value) {
        this.invoiceNo = value;
    }

    /**
     * Gets the value of the invoiceItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the invoiceItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInvoiceItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InvoiceItem }
     * 
     * 
     */
    public List<InvoiceItem> getInvoiceItems() {
        if (invoiceItems == null) {
            invoiceItems = new ArrayList<InvoiceItem>();
        }
        return this.invoiceItems;
    }

    /**
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerName(String value) {
        this.customerName = value;
    }

    /**
     * Gets the value of the testMode property.
     * 
     */
    public boolean isTestMode() {
        return testMode;
    }

    /**
     * Sets the value of the testMode property.
     * 
     */
    public void setTestMode(boolean value) {
        this.testMode = value;
    }

    /**
     * Gets the value of the customerAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getCustomerAddress() {
        return customerAddress;
    }

    /**
     * Sets the value of the customerAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setCustomerAddress(Address value) {
        this.customerAddress = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the value of the billingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setBillingAddress(Address value) {
        this.billingAddress = value;
    }

    /**
     * Gets the value of the customerVatNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerVatNo() {
        return customerVatNo;
    }

    /**
     * Sets the value of the customerVatNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerVatNo(String value) {
        this.customerVatNo = value;
    }

    /**
     * Gets the value of the billingEntityVatNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingEntityVatNo() {
        return billingEntityVatNo;
    }

    /**
     * Sets the value of the billingEntityVatNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingEntityVatNo(String value) {
        this.billingEntityVatNo = value;
    }

    /**
     * Gets the value of the taxRate property.
     * 
     */
    public double getTaxRate() {
        return taxRate;
    }

    /**
     * Sets the value of the taxRate property.
     * 
     */
    public void setTaxRate(double value) {
        this.taxRate = value;
    }

    /**
     * Gets the value of the pdfRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfRef() {
        return pdfRef;
    }

    /**
     * Sets the value of the pdfRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfRef(String value) {
        this.pdfRef = value;
    }

    /**
     * Gets the value of the paymentMethodInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodInstanceName() {
        return paymentMethodInstanceName;
    }

    /**
     * Sets the value of the paymentMethodInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodInstanceName(String value) {
        this.paymentMethodInstanceName = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceStatus }
     *     
     */
    public InvoiceStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceStatus }
     *     
     */
    public void setStatus(InvoiceStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDueDate(Long value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the cutOffDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCutOffDate() {
        return cutOffDate;
    }

    /**
     * Sets the value of the cutOffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCutOffDate(Long value) {
        this.cutOffDate = value;
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
     * Gets the value of the currencyId property.
     * 
     */
    public long getCurrencyId() {
        return currencyId;
    }

    /**
     * Sets the value of the currencyId property.
     * 
     */
    public void setCurrencyId(long value) {
        this.currencyId = value;
    }

    /**
     * Gets the value of the customerUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerUUID() {
        return customerUUID;
    }

    /**
     * Sets the value of the customerUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerUUID(String value) {
        this.customerUUID = value;
    }

    /**
     * Gets the value of the creditNote property.
     * 
     */
    public boolean isCreditNote() {
        return creditNote;
    }

    /**
     * Sets the value of the creditNote property.
     * 
     */
    public void setCreditNote(boolean value) {
        this.creditNote = value;
    }

    /**
     * Gets the value of the transactionUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionUUID() {
        return transactionUUID;
    }

    /**
     * Sets the value of the transactionUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionUUID(String value) {
        this.transactionUUID = value;
    }

    /**
     * Gets the value of the pdf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdf() {
        return pdf;
    }

    /**
     * Sets the value of the pdf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdf(String value) {
        this.pdf = value;
    }

}
