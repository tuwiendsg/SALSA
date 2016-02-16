package at.ac.tuwien.dsg.cloud.salsa.model.CloudServices;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import java.util.List;

public class CloudService {
    private AccessPoint connectVia;    
    private DataCenter dataCenter;

    public CloudService() {
    }

    public AccessPoint getConnectVia() {
        return connectVia;
    }

    public void setConnectVia(AccessPoint connectVia) {
        this.connectVia = connectVia;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    
}
