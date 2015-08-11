package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.NodeEntity;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic.GenericServiceUnit;

@NodeEntity
public abstract class ServiceEntity extends GenericServiceUnit {

//    @GraphId
//    protected Long graphID;
//    @Indexed(unique = true)
//    @BusinessId
//    protected String id;
//
//    protected String name;
//    protected Set<SyblDirective> directives = new HashSet<>();

    public ServiceEntity() {
    }

    public ServiceEntity(String id) {
        this.id = id;
    }

    public ServiceEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

//    public void addDirective(SyblDirective directive) {
//        if (directives == null) {
//            directives = new HashSet<>();
//        }
//        directives.add(directive);
//    }
//
//    // GENERATED METHODS
//    public Set<SyblDirective> getDirectives() {
//        return directives;
//    }
//
//    public void setDirectives(Set<SyblDirective> directives) {
//        this.directives = directives;
//    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getName() {
//        return name;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServiceEntity other = (ServiceEntity) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
