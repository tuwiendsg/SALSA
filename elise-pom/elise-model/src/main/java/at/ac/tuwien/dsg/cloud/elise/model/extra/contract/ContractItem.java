package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class ContractItem {

    @GraphId
    Long graphID;
    private String name;

    private String service;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
