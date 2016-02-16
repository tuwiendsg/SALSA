package at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource;

import java.util.ArrayList;
import java.util.List;

public class NetworkService {

    private List<VNFForwardGraph> vnfForwardGraphs;
    private List<AccessPoint> accessPoints;

    public NetworkService() {
    }

    public List<VNFForwardGraph> getVnfForwardGraphs() {
        if (vnfForwardGraphs == null) {
            vnfForwardGraphs = new ArrayList<>();
        }
        return vnfForwardGraphs;
    }

    public void setVnfForwardGraphs(List<VNFForwardGraph> vnfForwardGraphs) {
        this.vnfForwardGraphs = vnfForwardGraphs;
    }

    public List<AccessPoint> getAccessPoints() {
        if (accessPoints == null) {
            this.accessPoints = new ArrayList<>();
        }
        return accessPoints;
    }

    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }

}
