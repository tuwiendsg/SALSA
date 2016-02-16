package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream;

public class ObservedProperty {

    /**
     * e.g. temperature, humidity, gps, light
     */
    String name;

    /**
     * e.g. temperature in celsius, fahrenheit, GPS in lat/long
     */
    String measureUnit;

    public ObservedProperty() {
    }

    public ObservedProperty(String name, String measureUnit) {
        this.name = name;
        this.measureUnit = measureUnit;
    }

    public String getName() {
        return name;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }
    
    

}
