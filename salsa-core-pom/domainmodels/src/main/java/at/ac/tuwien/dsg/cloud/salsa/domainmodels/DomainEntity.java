/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.domainmodels;

import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.DockerInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IoT.GatewayInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IoT.SensorInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.Middleware.DatabaseManagementInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.Middleware.WebContainerInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.ElasticPlatformServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.ExecutableAppInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.SystemServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.application.WebAppInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Duc-Hung LE
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = VirtualMachineInfo.class, name = "IaaS_VM"),
    @JsonSubTypes.Type(value = DockerInfo.class, name = "IaaS_Docker"),
    @JsonSubTypes.Type(value = SensorInfo.class, name = "IoT_Sensor"),
    @JsonSubTypes.Type(value = GatewayInfo.class, name = "IoT_Gateway"),
    @JsonSubTypes.Type(value = WebContainerInfo.class, name = "Middleware_Webcontainer"),
    @JsonSubTypes.Type(value = DatabaseManagementInfo.class, name = "Middleware_DatabaseManagement"),
    @JsonSubTypes.Type(value = ElasticPlatformServiceInfo.class, name = "App_ElasticPlatformService"),
    @JsonSubTypes.Type(value = ExecutableAppInfo.class, name = "App_ExecutableApp"),
    @JsonSubTypes.Type(value = SystemServiceInfo.class, name = "App_SystemService"),
    @JsonSubTypes.Type(value = WebAppInfo.class, name = "App_WebApp")
})
public class DomainEntity {

    protected String domainID;
    protected String name;
    protected ServiceCategory category;
    protected Set<String> states;
    protected String currentState;

    public DomainEntity() {

    }

    public DomainEntity(ServiceCategory category, String domainID, String name, String... states) {

        this.category = category;
        this.domainID = domainID;
        this.name = name;
        // default states
        this.states = new HashSet<>();
        // add if the domain does not include this
        hasState("undeployed");
        hasState("error");

        if (states.length > 0) {
            this.states = new HashSet(Arrays.asList(states));
        }
    }

    public void updateStateList(Object[] theStates) {
        for (Object s : theStates) {
            this.hasState(s.toString());
        }
    }

    public Set<String> getStates() {
        return states;
    }

    public DomainEntity hasState(String state) {
        if (this.states == null) {
            this.states = new HashSet<>();
        }
        this.states.add(state);
        return this;
    }

    public String getDomainID() {
        return domainID;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    public String getName() {
        return name;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(DomainEntity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static DomainEntity fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.readValue(json, DomainEntity.class);
        } catch (IOException ex) {
            Logger.getLogger(DomainEntity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.domainID);
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.category);
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
        final DomainEntity other = (DomainEntity) obj;
        if (!Objects.equals(this.domainID, other.domainID)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.category != other.category) {
            return false;
        }
        return true;
    }

}
