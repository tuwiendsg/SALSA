package at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource;


import java.util.ArrayList;
import java.util.List;

public class VNF {

    /**
     * The VNF usually takes the same name of VM or container.
     */
    String name;

    /**
     * Setting of the router
     */
    String routingProtocol;

    /**
     * List of neighbour router
     */
    List<VNF> peers;

    /**
     * List of live hosts that are allocated. This can hints of which hosts is being routed via this router
     */
    List<String> dns;

    /**
     * List of interface which can be configured as access point
     */
    List<AccessPoint> networkInterface;

    public void connectWith(VNF vnf) {
        if (peers == null) {
            peers = new ArrayList<>();
        }
        peers.add(vnf);
    }

    public void disconnectWith(VNF vnf) {
        if (peers != null) {
            peers.remove(vnf);
        }
    }

    public VNF() {
    }

    public VNF(String name, String routingProtocol, AccessPoint networkInterface) {
        this.name = name;
        this.routingProtocol = routingProtocol;
        this.networkInterface = new ArrayList<>();
        this.networkInterface.add(networkInterface);
    }

    public String getRoutingProtocol() {
        return routingProtocol;
    }

    public void setRoutingProtocol(String routingProtocol) {
        this.routingProtocol = routingProtocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VNF> getPeers() {
        return peers;
    }

    public void setPeers(List<VNF> peers) {
        this.peers = peers;
    }

    public List<String> getDns() {
        return dns;
    }

    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    public List<AccessPoint> getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(List<AccessPoint> networkInterface) {
        this.networkInterface = networkInterface;
    }

}
