package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.Set;

public class ContractTerm {

    private String name;

    private ContractTermType type;

    private Set<Constraint> contraints;

    public ContractTermType getType() {
        return type;
    }

    public void setType(ContractTermType type) {
        this.type = type;
    }

    public Set<Constraint> getContraints() {
        return contraints;
    }

    public void setContraints(Set<Constraint> contraints) {
        this.contraints = contraints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
