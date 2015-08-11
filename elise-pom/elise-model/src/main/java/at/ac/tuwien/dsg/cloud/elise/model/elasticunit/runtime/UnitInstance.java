package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime;

import org.springframework.data.neo4j.annotation.NodeEntity;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.GenericServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.Metric;
import at.ac.tuwien.dsg.cloud.elise.model.type.State;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * The instance managed by different cloud and management services
 * @author hungld
 */
//@NodeEntity
public class UnitInstance extends GenericServiceUnit {

    protected String identification;
    protected State state;

    public UnitInstance() {
    }

    public UnitInstance(String name, ServiceCategory category, State state) {
        super(name, category);
        this.state = state;
    }

    // GENERATED METHODS
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public Set<Metric> findAllMetricValues(){
        return new HashSet<>();
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    public void mergeWith(UnitInstance otherInstance){
        this.getCapabilities().addAll(otherInstance.getCapabilities());
        this.setState(otherInstance.getState());
        // TODO: do actual merging
      //  this.getDomainInfo().addAll(otherInstance.getDomainInfo());        
    }
    

}
