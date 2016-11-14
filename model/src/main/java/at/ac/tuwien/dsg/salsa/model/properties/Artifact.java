/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.properties;

/**
 *
 * @author hungld
 */
public class Artifact {

    public enum RepoType {
        HTTP, GIT
    }

    Long id;

    String name;
    String artifactType;
    String reference;
    RepoType repoType = RepoType.HTTP; // default, how to download
    String tags;

    public Artifact(String name, String artifactType, String reference) {
        this.name = name;
        this.artifactType = artifactType;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public RepoType getRepoType() {
        return repoType;
    }

    public void setRepoType(RepoType repoType) {
        this.repoType = repoType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
