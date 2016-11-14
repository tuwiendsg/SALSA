/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.messaging.model.Elise;

/**
 *
 * @author Duc-Hung Le
 *
 */
public class EliseQueryRule {

    String metric;
    String value;
    OPERATION operation = OPERATION.EQUAL;

    public enum OPERATION {

        EQUAL,
        GREATER, LESSER,
        GREATER_OR_EQUAL, LESSER_OR_EQUAL
    }

    public EliseQueryRule() {
    }

    public EliseQueryRule(String metric, String value, OPERATION operation) {
        this.metric = metric;
        this.value = value;
        this.operation = operation;
    }

    public String getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }

    public OPERATION getOperation() {
        return operation;
    }

    // check a value if fulfilled, do not care about metric
    public boolean isFulfilled(Object avalue) {

        if (this.operation.equals(OPERATION.EQUAL)) {
            return value.equals(avalue);
        }
        try {
            double d1 = Double.parseDouble(value);
            double d2 = Double.parseDouble(avalue.toString());
            System.out.println("Comapring: " + value +" and " + avalue.toString());

            switch (this.operation) {
                case GREATER:
                    return d1 > d2;
                case GREATER_OR_EQUAL:
                    return d1 >= d2;
                case LESSER:
                    return d1 < d2;
                case LESSER_OR_EQUAL:
                    return d1 <= d2;
                default:
                    return false;
            }
        } catch (NullPointerException | NumberFormatException e1) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "EliseQueryRule{"+metric +"/"+ operation + "/" + value +"}";
    }
    
    

}
