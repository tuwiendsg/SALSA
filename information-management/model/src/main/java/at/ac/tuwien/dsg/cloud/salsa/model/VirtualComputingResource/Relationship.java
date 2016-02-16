package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource;


public class Relationship {
    private SoftwareDefineGateway source;
    private SoftwareDefineGateway target;
    private RelationshipType type;

    public Relationship() {
    }

    public Relationship(SoftwareDefineGateway source, SoftwareDefineGateway target, RelationshipType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public SoftwareDefineGateway getSource() {
        return source;
    }

    public void setSource(SoftwareDefineGateway source) {
        this.source = source;
    }

    public SoftwareDefineGateway getTarget() {
        return target;
    }

    public void setTarget(SoftwareDefineGateway target) {
        this.target = target;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }
    
    
}
