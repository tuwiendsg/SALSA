package at.ac.tuwien.dsg.salsa.model.extra.contract;

import java.util.Set;

public class ContractTemplate {

    Long id;
    private String name;

    private Set<String> terms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTerms() {
        return terms;
    }

    public void setTerms(Set<String> terms) {
        this.terms = terms;
    }

}
