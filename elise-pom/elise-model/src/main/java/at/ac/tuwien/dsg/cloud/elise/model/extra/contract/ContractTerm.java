package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class ContractTerm {
    
    @GraphId
    Long graphID;

    private String name;

    private String type;

    @RelatedTo
    @Fetch
    private Set<Constraint> constraints;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(Set<Constraint> constraints) {
        if (this.constraints == null) {
            this.constraints = new HashSet<>();
        }
        this.constraints = constraints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
