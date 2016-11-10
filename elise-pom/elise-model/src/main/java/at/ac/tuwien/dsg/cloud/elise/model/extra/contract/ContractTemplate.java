package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.Set;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class ContractTemplate {

    @GraphId
    Long graphID;
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
