
package com.weaveworks.weave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "Protocol",
    "ProtocolMinVersion",
    "ProtocolMaxVersion",
    "Encryption",
    "PeerDiscovery",
    "Name",
    "NickName",
    "Port",
    "Peers",
    "UnicastRoutes",
    "BroadcastRoutes",
    "Connections",
    "Targets",
    "OverlayDiagnostics",
    "TrustedSubnets",
    "Interface",
    "CaptureStats",
    "MACs"
})
public class Router {

    @JsonProperty("Protocol")
    private String Protocol;
    @JsonProperty("ProtocolMinVersion")
    private int ProtocolMinVersion;
    @JsonProperty("ProtocolMaxVersion")
    private int ProtocolMaxVersion;
    @JsonProperty("Encryption")
    private boolean Encryption;
    @JsonProperty("PeerDiscovery")
    private boolean PeerDiscovery;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("NickName")
    private String NickName;
    @JsonProperty("Port")
    private int Port;
    @JsonProperty("Peers")
    private List<Peer> Peers = new ArrayList<Peer>();
    @JsonProperty("UnicastRoutes")
    private List<UnicastRoute> UnicastRoutes = new ArrayList<UnicastRoute>();
    @JsonProperty("BroadcastRoutes")
    private List<BroadcastRoute> BroadcastRoutes = new ArrayList<BroadcastRoute>();
    @JsonProperty("Connections")
    private List<Connection_Peer> Connections = new ArrayList<Connection_Peer>();
    @JsonProperty("Targets")
    private Object Targets;
    @JsonProperty("OverlayDiagnostics")
    private com.weaveworks.weave.OverlayDiagnostics OverlayDiagnostics;
    @JsonProperty("TrustedSubnets")
    private List<Object> TrustedSubnets = new ArrayList<Object>();
    @JsonProperty("Interface")
    private String Interface;
    @JsonProperty("CaptureStats")
    private com.weaveworks.weave.CaptureStats CaptureStats;
    @JsonProperty("MACs")
    private List<MAC> MACs = new ArrayList<MAC>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Router() {
    }

    /**
     * 
     * @param CaptureStats
     * @param UnicastRoutes
     * @param OverlayDiagnostics
     * @param ProtocolMinVersion
     * @param NickName
     * @param TrustedSubnets
     * @param Encryption
     * @param BroadcastRoutes
     * @param ProtocolMaxVersion
     * @param Interface
     * @param Name
     * @param Peers
     * @param Port
     * @param PeerDiscovery
     * @param MACs
     * @param Targets
     * @param Protocol
     * @param Connections
     */
    public Router(String Protocol, int ProtocolMinVersion, int ProtocolMaxVersion, boolean Encryption, boolean PeerDiscovery, String Name, String NickName, int Port, List<Peer> Peers, List<UnicastRoute> UnicastRoutes, List<BroadcastRoute> BroadcastRoutes, List<Connection_Peer> Connections, Object Targets, com.weaveworks.weave.OverlayDiagnostics OverlayDiagnostics, List<Object> TrustedSubnets, String Interface, com.weaveworks.weave.CaptureStats CaptureStats, List<MAC> MACs) {
        this.Protocol = Protocol;
        this.ProtocolMinVersion = ProtocolMinVersion;
        this.ProtocolMaxVersion = ProtocolMaxVersion;
        this.Encryption = Encryption;
        this.PeerDiscovery = PeerDiscovery;
        this.Name = Name;
        this.NickName = NickName;
        this.Port = Port;
        this.Peers = Peers;
        this.UnicastRoutes = UnicastRoutes;
        this.BroadcastRoutes = BroadcastRoutes;
        this.Connections = Connections;
        this.Targets = Targets;
        this.OverlayDiagnostics = OverlayDiagnostics;
        this.TrustedSubnets = TrustedSubnets;
        this.Interface = Interface;
        this.CaptureStats = CaptureStats;
        this.MACs = MACs;
    }

    /**
     * 
     * @return
     *     The Protocol
     */
    @JsonProperty("Protocol")
    public String getProtocol() {
        return Protocol;
    }

    /**
     * 
     * @param Protocol
     *     The Protocol
     */
    @JsonProperty("Protocol")
    public void setProtocol(String Protocol) {
        this.Protocol = Protocol;
    }

    /**
     * 
     * @return
     *     The ProtocolMinVersion
     */
    @JsonProperty("ProtocolMinVersion")
    public int getProtocolMinVersion() {
        return ProtocolMinVersion;
    }

    /**
     * 
     * @param ProtocolMinVersion
     *     The ProtocolMinVersion
     */
    @JsonProperty("ProtocolMinVersion")
    public void setProtocolMinVersion(int ProtocolMinVersion) {
        this.ProtocolMinVersion = ProtocolMinVersion;
    }

    /**
     * 
     * @return
     *     The ProtocolMaxVersion
     */
    @JsonProperty("ProtocolMaxVersion")
    public int getProtocolMaxVersion() {
        return ProtocolMaxVersion;
    }

    /**
     * 
     * @param ProtocolMaxVersion
     *     The ProtocolMaxVersion
     */
    @JsonProperty("ProtocolMaxVersion")
    public void setProtocolMaxVersion(int ProtocolMaxVersion) {
        this.ProtocolMaxVersion = ProtocolMaxVersion;
    }

    /**
     * 
     * @return
     *     The Encryption
     */
    @JsonProperty("Encryption")
    public boolean isEncryption() {
        return Encryption;
    }

    /**
     * 
     * @param Encryption
     *     The Encryption
     */
    @JsonProperty("Encryption")
    public void setEncryption(boolean Encryption) {
        this.Encryption = Encryption;
    }

    /**
     * 
     * @return
     *     The PeerDiscovery
     */
    @JsonProperty("PeerDiscovery")
    public boolean isPeerDiscovery() {
        return PeerDiscovery;
    }

    /**
     * 
     * @param PeerDiscovery
     *     The PeerDiscovery
     */
    @JsonProperty("PeerDiscovery")
    public void setPeerDiscovery(boolean PeerDiscovery) {
        this.PeerDiscovery = PeerDiscovery;
    }

    /**
     * 
     * @return
     *     The Name
     */
    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    /**
     * 
     * @param Name
     *     The Name
     */
    @JsonProperty("Name")
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * 
     * @return
     *     The NickName
     */
    @JsonProperty("NickName")
    public String getNickName() {
        return NickName;
    }

    /**
     * 
     * @param NickName
     *     The NickName
     */
    @JsonProperty("NickName")
    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    /**
     * 
     * @return
     *     The Port
     */
    @JsonProperty("Port")
    public int getPort() {
        return Port;
    }

    /**
     * 
     * @param Port
     *     The Port
     */
    @JsonProperty("Port")
    public void setPort(int Port) {
        this.Port = Port;
    }

    /**
     * 
     * @return
     *     The Peers
     */
    @JsonProperty("Peers")
    public List<Peer> getPeers() {
        return Peers;
    }

    /**
     * 
     * @param Peers
     *     The Peers
     */
    @JsonProperty("Peers")
    public void setPeers(List<Peer> Peers) {
        this.Peers = Peers;
    }

    /**
     * 
     * @return
     *     The UnicastRoutes
     */
    @JsonProperty("UnicastRoutes")
    public List<UnicastRoute> getUnicastRoutes() {
        return UnicastRoutes;
    }

    /**
     * 
     * @param UnicastRoutes
     *     The UnicastRoutes
     */
    @JsonProperty("UnicastRoutes")
    public void setUnicastRoutes(List<UnicastRoute> UnicastRoutes) {
        this.UnicastRoutes = UnicastRoutes;
    }

    /**
     * 
     * @return
     *     The BroadcastRoutes
     */
    @JsonProperty("BroadcastRoutes")
    public List<BroadcastRoute> getBroadcastRoutes() {
        return BroadcastRoutes;
    }

    /**
     * 
     * @param BroadcastRoutes
     *     The BroadcastRoutes
     */
    @JsonProperty("BroadcastRoutes")
    public void setBroadcastRoutes(List<BroadcastRoute> BroadcastRoutes) {
        this.BroadcastRoutes = BroadcastRoutes;
    }

    /**
     * 
     * @return
     *     The Connections
     */
    @JsonProperty("Connections")
    public List<Connection_Peer> getConnections() {
        return Connections;
    }

    /**
     * 
     * @param Connections
     *     The Connections
     */
    @JsonProperty("Connections")
    public void setConnections(List<Connection_Peer> Connections) {
        this.Connections = Connections;
    }

    /**
     * 
     * @return
     *     The Targets
     */
    @JsonProperty("Targets")
    public Object getTargets() {
        return Targets;
    }

    /**
     * 
     * @param Targets
     *     The Targets
     */
    @JsonProperty("Targets")
    public void setTargets(Object Targets) {
        this.Targets = Targets;
    }

    /**
     * 
     * @return
     *     The OverlayDiagnostics
     */
    @JsonProperty("OverlayDiagnostics")
    public com.weaveworks.weave.OverlayDiagnostics getOverlayDiagnostics() {
        return OverlayDiagnostics;
    }

    /**
     * 
     * @param OverlayDiagnostics
     *     The OverlayDiagnostics
     */
    @JsonProperty("OverlayDiagnostics")
    public void setOverlayDiagnostics(com.weaveworks.weave.OverlayDiagnostics OverlayDiagnostics) {
        this.OverlayDiagnostics = OverlayDiagnostics;
    }

    /**
     * 
     * @return
     *     The TrustedSubnets
     */
    @JsonProperty("TrustedSubnets")
    public List<Object> getTrustedSubnets() {
        return TrustedSubnets;
    }

    /**
     * 
     * @param TrustedSubnets
     *     The TrustedSubnets
     */
    @JsonProperty("TrustedSubnets")
    public void setTrustedSubnets(List<Object> TrustedSubnets) {
        this.TrustedSubnets = TrustedSubnets;
    }

    /**
     * 
     * @return
     *     The Interface
     */
    @JsonProperty("Interface")
    public String getInterface() {
        return Interface;
    }

    /**
     * 
     * @param Interface
     *     The Interface
     */
    @JsonProperty("Interface")
    public void setInterface(String Interface) {
        this.Interface = Interface;
    }

    /**
     * 
     * @return
     *     The CaptureStats
     */
    @JsonProperty("CaptureStats")
    public com.weaveworks.weave.CaptureStats getCaptureStats() {
        return CaptureStats;
    }

    /**
     * 
     * @param CaptureStats
     *     The CaptureStats
     */
    @JsonProperty("CaptureStats")
    public void setCaptureStats(com.weaveworks.weave.CaptureStats CaptureStats) {
        this.CaptureStats = CaptureStats;
    }

    /**
     * 
     * @return
     *     The MACs
     */
    @JsonProperty("MACs")
    public List<MAC> getMACs() {
        return MACs;
    }

    /**
     * 
     * @param MACs
     *     The MACs
     */
    @JsonProperty("MACs")
    public void setMACs(List<MAC> MACs) {
        this.MACs = MACs;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
