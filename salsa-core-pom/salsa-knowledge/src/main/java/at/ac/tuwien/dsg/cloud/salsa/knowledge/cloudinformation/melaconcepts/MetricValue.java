/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 \#317790)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts;

import java.io.Serializable;
import javax.xml.bind.annotation.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Author: Daniel Moldovan 
 * E-Mail: d.moldovan@dsg.tuwien.ac.at 

 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricValue")
public class MetricValue implements Comparable<MetricValue>,  Serializable {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "ValueType")
    @XmlEnum
    public enum ValueType implements Serializable{

        @XmlEnumValue("NUMERIC") NUMERIC,
        @XmlEnumValue("TEXT")TEXT,
        @XmlEnumValue("ENUMERATION")ENUM
    }
    @XmlElement(name = "Value", required = true)
    private Object value;
    @XmlAttribute(name = "ValueType", required = true)
    private ValueType valueType;

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public MetricValue(Object value) {
        this.value = value;
        if (value instanceof Number) {
            valueType = ValueType.NUMERIC;
        } else if (value instanceof String) {
            String string = value.toString();
            if (string.contains(",")) {
                valueType = ValueType.ENUM;
            } else {
                valueType = ValueType.TEXT;
            }
        } else {
            System.out.println("Unknown elements: " + value);
        }
    }

    public MetricValue() {
    }

    public Object getValue() {
        return value;
    }

    public String getValueRepresentation() {
        if (value instanceof Number) {
            String a;
            DecimalFormat df = new DecimalFormat("0.####");

            String formatted = df.format(value);
//            int index = formatted.indexOf(".");
//
//            if (index != -1) {
//                String[] parts = formatted.split("\\.");
//                String rest = "";
//                int zeroesCount = 0;
//                for (char c : parts[1].toCharArray()) {
//                    if (c == '0') {
//                        zeroesCount++;
//                    } else {
//                        for (int i = 0; i < zeroesCount; i++) {
//                            rest += '0';
//                        }
//                        rest += c;
//                        break;
//                    }
//                }
//                a = (rest.length() > 0) ? parts[0] + "." + rest : parts[0];
//            } else {
//                a = formatted;
//            }
//            return a;
            return formatted;
        } else if (value instanceof String) {
            return value.toString();
        } else {
            System.out.println("Unknown elements: " + value);
            return "-1";
        }
    }

    public MetricValue clone() {
//        Object cloneValue = null;
//        if (  value instanceof Number) {
//            cloneValue = ((Number)value).doubleValue();
//        } else if  (value instanceof String) {
//            cloneValue =  value;
//        }
//        return new MetricValue(cloneValue);
        return new MetricValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
        if (value instanceof Number) {
            valueType = ValueType.NUMERIC;
        } else if (value instanceof String) {
            String string = value.toString();
            if (string.contains(",")) {
                valueType = ValueType.ENUM;
            } else {
                valueType = ValueType.TEXT;
            }
        } else {
            System.out.println("Unknown elements: " + value);
        }
    }

    public void sum(MetricValue metricValue) {
        if (value instanceof Number) {
            double oldVal = ((Number) value).doubleValue();
            double newVal = ((Number) metricValue.getValue()).doubleValue();
            this.value = oldVal + newVal;
        }
    }

    public void divide(int size) {
        if (value instanceof Number) {
            double oldVal = ((Number) value).doubleValue();
            this.value = oldVal / size;

        }
    }

    /**
     * @param o
     * @return Used to compare different metric values. Currently is returns 0
     * if the values can;t be compared. -1 if this smaller than argument, 1 if
     * greater, 0 if equal or can't compare
     */
    public int compareTo(MetricValue o) {

        Object otherValue = o.getValue();
        switch (valueType) {
            case NUMERIC:
                return new Double(((Number) value).doubleValue()).compareTo(((Number) otherValue).doubleValue());
            case TEXT:
                return ((String) value).compareTo((String) otherValue);
            case ENUM:
                return value.toString().compareTo(otherValue.toString());
            default:
                System.err.println("Incomparable elements: " + value + ", " + otherValue);
                return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetricValue that = (MetricValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
