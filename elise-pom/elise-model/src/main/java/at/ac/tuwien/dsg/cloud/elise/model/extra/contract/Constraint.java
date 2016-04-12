package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.Set;

public class Constraint {

    private String name;

    private String type;

    private Script enforcementScript;

    private Script compositionScript;

    private String description;

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

    public Script getEnforcementScript() {
        return enforcementScript;
    }

    public void setEnforcementScript(Script enforcementScript) {
        this.enforcementScript = enforcementScript;
    }

    public Script getCompositionScript() {
        return compositionScript;
    }

    public void setCompositionScript(Script compositionScript) {
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
        this.parameters = parameters;
    }

}
