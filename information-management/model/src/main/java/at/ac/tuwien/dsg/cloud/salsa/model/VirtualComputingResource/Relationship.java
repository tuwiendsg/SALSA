package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource;


public class Relationship {
    private SoftwareDefinedGateway source;
    private SoftwareDefinedGateway target;
    private RelationshipType type;

    public Relationship() {
    }

    public Relationship(SoftwareDefinedGateway source, SoftwareDefinedGateway target, RelationshipType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public SoftwareDefinedGateway getSource() {
        return source;
    }

    public void setSource(SoftwareDefinedGateway source) {
        this.source = source;
    }

    public SoftwareDefinedGateway getTarget() {
        return target;
    }

    public void setTarget(SoftwareDefinedGateway target) {
        this.target = target;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }
    
    
}
