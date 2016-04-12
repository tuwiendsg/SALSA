package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class MetaData {

    @GraphId
    Long graphID;

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
