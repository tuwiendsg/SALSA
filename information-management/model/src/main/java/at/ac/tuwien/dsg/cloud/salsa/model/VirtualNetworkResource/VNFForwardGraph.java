package at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource;

import java.util.ArrayList;
import java.util.List;

public class VNFForwardGraph {

    private List<VNF> nodes;
    private List<VNFLink> links;

    public static class VNFLink {

        String source;
        String target;
        String bandwidth;

        public VNFLink() {
        }

        public VNFLink(String source, String target, String bandwidth) {
            this.source = source;
            this.target = target;
            this.bandwidth = bandwidth;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(String bandwidth) {
            this.bandwidth = bandwidth;
        }

    }

    public VNFForwardGraph() {
    }

    public List<VNF> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        return nodes;
    }

    public void setNodes(List<VNF> nodes) {
        this.nodes = nodes;
    }

    public List<VNFLink> getLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
        return links;
    }

    public void setLinks(List<VNFLink> links) {
        this.links = links;
    }

}
