/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import at.ac.tuwien.dsg.cloud.elise.model.HasUniqueId;
import java.util.Collection;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.neo4j.annotation.GraphId;

/**
 *
 * @author hungld
 */
@NodeEntity
public class Properties implements HasUniqueId {

    @GraphId
    Long graphID;

    @Indexed(unique = true)
    protected String id;

    protected String name;

    @Fetch
    protected Set<Metric> metrics;

    // e.g. Flavor has CPU, RAM   
    @RelatedTo(direction = Direction.OUTGOING)
    @Fetch
    protected Set<Properties> underlyingFeatures;

    {
        metrics = new HashSet<>();
        underlyingFeatures = new HashSet<>();
    }

    public Properties() {
    }

    public Properties(String name) {
        this.name = name;
    }

    public Properties hasUnderlyingFeature(Properties feature) {
        feature.setIdByParentNameRecursively(this.getId());
        this.underlyingFeatures.add(feature);
        return this;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Properties> getUnderlyingFeatures() {
        return underlyingFeatures;
    }

    public void setUnderlyingFeatures(Set<Properties> underlyingFeatures) {
        for (Properties f : underlyingFeatures) {
            f.setIdByParentName(this.id);
        }
        this.underlyingFeatures = underlyingFeatures;
    }

    public void setIdByParentName(String parentID) {
        this.id = parentID + "." + this.name;
    }

    public void setIdByParentNameRecursively(String parentID) {
        this.id = parentID + "." + this.name;
        for (Properties f : this.underlyingFeatures) {
            f.setIdByParentNameRecursively(this.id);
        }
    }

    public String getName() {
        return name;
    }

    public Metric getMetricValueByName(String name) {
        for (Metric mv : this.metrics) {
            if (mv.getName().equals(name)) {
                return mv;
            }
        }
        return null;
    }

    // the feature are equal if they have the same IDs
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Properties other = (Properties) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Set<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<Metric> metrics) {
        this.metrics = metrics;
    }
    
    public Properties hasMetric(Metric metric){
        this.metrics.add(metric);
        return this;
    }

}
