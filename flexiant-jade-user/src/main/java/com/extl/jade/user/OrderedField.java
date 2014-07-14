
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The orderedField class holds a field to order a query by, together with the direction of the ordering.</p><p>The orderedField complex type is used within a queryLimit complex type to specify the sort order of the results returned. Each orderedField specifies a field name and a sort order, and the queryLimit contains an array of orderedField complex types, which are composed to generate a sort order.</p><p>The fieldName is the name of the field, being the member variable name within the relevant returned complex type. The sortOrder is an enum of type resultOrder; using a sortOrder of ASC sorts that field in ascending order, and a sortOrder of DESC sorts it in descending order.</p>
 * 
 * <p>Java class for orderedField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="orderedField">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="aggregationFunction" type="{http://extility.flexiant.net}aggregation" minOccurs="0"/>
 *         &lt;element name="fieldName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sortOrder" type="{http://extility.flexiant.net}resultOrder" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orderedField", propOrder = {
    "aggregationFunction",
    "fieldName",
    "sortOrder"
})
public class OrderedField {

    protected Aggregation aggregationFunction;
    protected String fieldName;
    protected ResultOrder sortOrder;

    /**
     * Gets the value of the aggregationFunction property.
     * 
     * @return
     *     possible object is
     *     {@link Aggregation }
     *     
     */
    public Aggregation getAggregationFunction() {
        return aggregationFunction;
    }

    /**
     * Sets the value of the aggregationFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Aggregation }
     *     
     */
    public void setAggregationFunction(Aggregation value) {
        this.aggregationFunction = value;
    }

    /**
     * Gets the value of the fieldName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the value of the fieldName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldName(String value) {
        this.fieldName = value;
    }

    /**
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link ResultOrder }
     *     
     */
    public ResultOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultOrder }
     *     
     */
    public void setSortOrder(ResultOrder value) {
        this.sortOrder = value;
    }

}
