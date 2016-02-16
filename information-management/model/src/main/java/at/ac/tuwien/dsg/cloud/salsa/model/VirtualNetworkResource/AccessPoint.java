package at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource;

public class AccessPoint {
    String networkAddress;    

    public AccessPoint() {
    }

    public AccessPoint(String networkAddress) {
        this.networkAddress = networkAddress;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }
    
    
}
