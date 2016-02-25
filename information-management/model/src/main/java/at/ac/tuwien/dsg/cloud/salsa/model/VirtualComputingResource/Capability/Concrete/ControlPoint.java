/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.CapabilityType;

/**
 * The class manage control point, which is an action on the resource.
 * Because the control need actual to execute something, it show
 * clear way to invoke the capability
 * 
 * @author hungld
 */
public class ControlPoint extends Capability {

    public static enum InvokeProtocol {
        GET, POST, DELETE, PUT
    }
    /**
     * How to call the capability
     */
    InvokeProtocol invokeProtocol;

    /**
     * The absolute path to the service, e.g. http://example.com/rest/start/{id} Note: The parameters are put in brackets
     */
    String reference;

    /**
     * The data using in POST and PUT
     */
    String postData;

//    Map<String, Object> controlStates;
    /**
     * **************
     * GETER/SETTER *
     ***************
     */
    public ControlPoint() {
        type = CapabilityType.ControlPoint;
    }
    
    public ControlPoint(String name, String description) {        
        super(name, CapabilityType.ControlPoint, description);
    }

    public ControlPoint(String name, String description, InvokeProtocol invokeProtocol, String reference, String postData) {
        super(name, CapabilityType.ControlPoint, description);
        this.invokeProtocol = invokeProtocol;
        this.reference = reference;
        this.postData = postData;
    }
    
    

    public InvokeProtocol getInvokeProtocol() {
        return invokeProtocol;
    }

    public void setInvokeProtocol(InvokeProtocol invokeProtocol) {
        this.invokeProtocol = invokeProtocol;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

}
