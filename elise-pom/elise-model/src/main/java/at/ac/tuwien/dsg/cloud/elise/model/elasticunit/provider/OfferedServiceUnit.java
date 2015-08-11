/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.GenericServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 *
 * @author hungld
 */
@NodeEntity
public class OfferedServiceUnit extends GenericServiceUnit {

    Set<CostFunction> costFuntions;

    @Fetch
    protected String providerID;
    
    @Fetch
    protected Set<Artifact> artifacts = new HashSet<>();

    ;

    public OfferedServiceUnit() {
    }

    public OfferedServiceUnit(String name, ServiceCategory category) {
        super(name, category);
    }

    public OfferedServiceUnit(String name, ServiceCategory category, String providerID) {
        super(name, category);
        this.providerID = providerID;
    }

    public OfferedServiceUnit hasCostFunction(CostFunction costFunction) {
        this.costFuntions.add(costFunction);
        return this;
    }

    public Set<CostFunction> getCostFuntions() {
        return costFuntions;
    }

    public void setCostFuntions(Set<CostFunction> costFuntions) {
        this.costFuntions = costFuntions;
    }

    public String getProviderID() {
        return this.providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public GenericServiceUnit hasArtifact(Artifact artifact) {
        this.artifacts.add(artifact);
        return this;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public Artifact findArtifactByName(String name) {
        for (Artifact art : this.artifacts) {
            if (art.getName().equals(name)) {
                return art;
            }
        }
        return null;
    }
}
