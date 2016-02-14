package VirtualResource;

import PhysicalResource.PhysicalResource;

import java.util.List;

import CloudServices.AccessPoint;

public class SoftwareDefineMachine {
    private String id;
    private String name;    
    /**
     * @associates <{VirtualResource.Capability}>
     */
    private List<Capability> capabilities;

    /**
     * @associates <{PhysicalResource.PhysicalResource}>
     */
    private List<PhysicalResource> physicalResources;
    private AccessPoint connectVia;

}
