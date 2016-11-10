package at.ac.tuwien.dsg.cloud.elise.model.extra.contract;

import java.util.Date;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Contract {

    @GraphId
    Long graphID;

    private String name;

    private String template;

    @Fetch
    private Set<ContractPartner> partners;

    @Fetch
    private Set<ContractItem> items;

    @Fetch
    private Set<Parameter> parameters;

    @Fetch
    private Set<MetaData> metaData;

    private String iotUnit;

    private Date validFrom;

    private Date invalidFrom;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Set<ContractPartner> getPartners() {
        return partners;
    }

    public void setPartners(Set<ContractPartner> partners) {
        this.partners = partners;
    }

    public Set<ContractItem> getItems() {
        return items;
    }

    public void setItems(Set<ContractItem> items) {
        this.items = items;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Set<MetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(Set<MetaData> metaData) {
        this.metaData = metaData;
    }

    public String getIotUnit() {
        return iotUnit;
    }

    public void setIotUnit(String iotUnit) {
        this.iotUnit = iotUnit;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getInvalidFrom() {
        return invalidFrom;
    }

    public void setInvalidFrom(Date invalidFrom) {
        this.invalidFrom = invalidFrom;
    }

}
