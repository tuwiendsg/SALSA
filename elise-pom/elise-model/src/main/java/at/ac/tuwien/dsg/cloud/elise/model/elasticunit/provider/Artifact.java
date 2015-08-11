/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.provider;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;



/**
 *
 * @author hungld
 */
@NodeEntity
public class Artifact {

    @GraphId
    Long graphID;
    
    String name;
    ArtifactType type;
    String reference;
    
    public enum ArtifactType{
        sh, 
        Dockerfile, 
        war, 
        jar, 
        misc
    }

    public Artifact() {
    }

    public Artifact(String name, ArtifactType type, String reference) {
        this.name = name;
        this.type = type;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public ArtifactType getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

}
