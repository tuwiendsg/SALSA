package at.ac.tuwien.dsg.salsa.model.extra.contract;

import java.util.HashSet;
import java.util.Set;

public class ContractTerm {

    Long id;

    private String name;

    private String type;

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
