package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream;

import at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource.PhysicalResource;
import java.util.concurrent.BlockingQueue;

/**
 * This class represents for Data Point management information
 */
public class DataStream {

    /**
     * A queue of data point
     */
    BlockingQueue<SimpleDataPoint> queue;

    /**
     * What is the data
     */
    ObservedProperty observedProperty;

    /**
     * Who produces the data
     */
    PhysicalResource physicalResource;

    /**
     * The type and reference to the buffer, where data stream can be read e.g. sensor can send data to an MQTT broker, or data is put into a local file
     */
    BUFFER_TYPE bufferType;
    String bufferReference;

    /**
     * Human reading metadata
     */
    String description;

    public enum BUFFER_TYPE {
        MQTT, AMPQ, LOCAL_FILE
    }

    /**
     * Construction and get/set
     */
    public DataStream() {
    }

    public DataStream(ObservedProperty observedProperty, BUFFER_TYPE bufferType, String bufferReference, String description) {
        this.observedProperty = observedProperty;        
        this.bufferType = bufferType;
        this.bufferReference = bufferReference;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BlockingQueue<SimpleDataPoint> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<SimpleDataPoint> queue) {
        this.queue = queue;
    }

    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
    }

    public PhysicalResource getPhysicalResource() {
        return physicalResource;
    }

    public void setPhysicalResource(PhysicalResource physicalResource) {
        this.physicalResource = physicalResource;
    }

    public BUFFER_TYPE getBufferType() {
        return bufferType;
    }

    public void setBufferType(BUFFER_TYPE bufferType) {
        this.bufferType = bufferType;
    }

    public String getBufferReference() {
        return bufferReference;
    }

    public void setBufferReference(String bufferReference) {
        this.bufferReference = bufferReference;
    }

}
