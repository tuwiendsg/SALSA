package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream;

import java.util.Date;

public class SimpleDataPoint {
    private Date timestamp;
    private Object value;

    public SimpleDataPoint() {
    }

    public SimpleDataPoint(Date timestamp, Object value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    
}
