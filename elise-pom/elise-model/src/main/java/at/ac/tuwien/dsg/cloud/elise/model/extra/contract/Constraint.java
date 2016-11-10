package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Constraint {
    
    @GraphId
    Long graphID;

    private String name;

    private String type;

    private String enforcementScript;

    private String compositionScript;

    private String description;

    @RelatedTo
    @Fetch
    private Set<ParameterTemplate> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnforcementScript() {
        return enforcementScript;
    }

    public void setEnforcementScript(String enforcementScript) {
        this.enforcementScript = enforcementScript;
    }

    public String getCompositionScript() {
        return compositionScript;
    }

    public void setCompositionScript(String compositionScript) {
        this.compositionScript = compositionScript;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ParameterTemplate> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ParameterTemplate> parameters) {
        if (this.parameters == null) {
            this.parameters = new HashSet<>();
        }
        this.parameters = parameters;
    }

}
