
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>The Aggregation class specifies an aggregation function that can be executed in a complex database query.</p><p>The database query acts on a particular type of resource or pseudo-resource, including unit transactions, unit transaction summaries, products, and product purchases. The query can optionally aggregate (e.g. produce sums, counts, minima, maxima and averages) and/or group by one or more fields. Field names are given in FQL dot notation.</p><p>The fields returned by the query will be those in the output array and those in the aggregation list. Putting a field within the 'group by' list will not cause it to appear in the output. All field names are given in FQL dot notation. In the query result, aggregated fields will appear with names such as SUM(disk.size), i.e. with the aggregation type enum value followed by the aggregated field in parenthesis.</p>
 * 
 * <p>Java class for aggregationField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aggregationField">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="aggregationFunction" type="{http://extility.flexiant.net}aggregation" minOccurs="0"/>
 *         &lt;element name="fieldName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aggregationField", propOrder = {
    "aggregationFunction",
    "fieldName"
})
public class AggregationField {

    protected Aggregation aggregationFunction;
    protected String fieldName;

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

}
