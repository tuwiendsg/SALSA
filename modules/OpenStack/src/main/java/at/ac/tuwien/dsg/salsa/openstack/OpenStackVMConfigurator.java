
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.openstack;

import at.ac.tuwien.dsg.salsa.model.enums.SalsaCommonActions;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.jclouds.ContextBuilder;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class OpenStackVMConfigurator implements ConfigurationModule {

    String sshkey;
    String username;
    String password;
    String tenant;
    String keystone_endpoint;
    String nova_api_endpoint;
    String region;

    NovaApi client;
    ServerApi serverApi;
    VolumeApi volumeApi;

    static Logger LOGGER = LoggerFactory.getLogger("BashModule");

    @Override
    public SalsaConfigureResult configureArtifact(SalsaConfigureTask configInfo, Map<String, String> parameters) {

        sshkey = parameters.get("openstack.sshkey");
        username = parameters.get("openstack.username");
        password = parameters.get("openstack.password");
        tenant = parameters.get("openstack.tenant");
        keystone_endpoint = parameters.get("openstack.keystone_endpoint");
        nova_api_endpoint = parameters.get("openstack.nova_api_endpoint");
        region = parameters.get("openstack.region");

        client = ContextBuilder.newBuilder("openstack-nova")
                .endpoint(nova_api_endpoint)
                .credentials(tenant + ":" + username, password)
                .buildApi(NovaApi.class);

        serverApi = client.getServerApiForZone(region);

        switch (configInfo.getActionName()) {
            case SalsaCommonActions.deploy: {
                return launchInstance(configInfo.getActionID(), parameters);
            }
            case SalsaCommonActions.undeploy: {
                return removeInstance(configInfo.getActionID(), parameters);
            }
            default:
                return new SalsaConfigureResult(configInfo.getActionName(), SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 1, "Unknown action name: " + configInfo.getActionName());
        }

    }

    private SalsaConfigureResult launchInstance(String actionID, Map<String, String> parameters) {
        // get parameters to create VM
        String instanceName = parameters.get("instancename");
        String imageId = parameters.get("imageid");
        String securityGroup = parameters.get("securityGroup");
        String sshKeyName = sshkey;
        String userData = parameters.get("userData");
        String instType = parameters.get("instType");
        String minInst = parameters.get("minInst");
        String maxInst = parameters.get("maxInst");

        // launch instance
        LOGGER.debug("Jclouds Openstack Connector for node name: " + instanceName + ", imageId: " + imageId + ", instanceType: " + instType + "sshKeyName: " + sshKeyName);
        CreateServerOptions createNodeOptions = new CreateServerOptions();

        createNodeOptions.userData(userData.getBytes());
        createNodeOptions.keyPairName(sshKeyName);

        LOGGER.debug("Jclouds Openstack - Prepare creation");

        ServerCreated serverCreated = serverApi.create(instanceName, imageId, instType, createNodeOptions);	// instance type is m1.small as default	    
        int maxtry = 20;
        int tryee = 0;
        LOGGER.debug("Jclouds Openstack - CREATED");
        LOGGER.debug("Jclouds Openstack - CREATED. ID = " + serverCreated.getId());

        while (getIpInstance(serverCreated.getId()) == null) {
            //serverApi.get(serverCreated.getId()).getStatus() != Status.ACTIVE){
            try {
                LOGGER.debug("Server " + serverCreated.getId() + " is not started.. wait 5 secs");
                Thread.sleep(5000);
                tryee += 1;
                if (tryee >= maxtry) {
                    SalsaConfigureResult result = new SalsaConfigureResult(actionID, SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 0, "VM created failed after " + maxtry + " tries. ID: " + serverCreated.getId());
                    return result;
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }
        SalsaConfigureResult result = new SalsaConfigureResult(actionID, SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "VM created. Server ID: " + serverCreated.getId());
        addEffectToLaunchResult(serverCreated.getId(), result);
        return result;
    }

    private void addEffectToLaunchResult(String instanceID, SalsaConfigureResult result) {
        Server server = serverApi.get(instanceID);
        Multimap<String, Address> map = server.getAddresses();
        List<Address> PA = (List<Address>) map.get("private");
        String privateIp = "";

        if (!PA.isEmpty()) {
            privateIp = PA.get(0).getAddr();
        }
        result.hasEffect("privateIp", privateIp);
        result.hasEffect("instanceid", instanceID);

        Server.Status status = server.getStatus();
        result.hasEffect("status", status.value());
    }

    private String getIpInstance(String instanceId) {
        LOGGER.debug("getIpInstance 1");
        Server server = serverApi.get(instanceId);
        LOGGER.debug("getIpInstance 2");
        Multimap<String, Address> map = server.getAddresses();
        if (map.isEmpty()) {
            return null;
        }
        LOGGER.debug("getIpInstance 3");
        List<Address> PA = (List<Address>) map.get("private");

        if (PA.isEmpty()) {
            return null;
        }
        LOGGER.debug("getIpInstance 4");
        return PA.get(0).getAddr();
    }

    public SalsaConfigureResult removeInstance(String actionID, Map<String, String> parameters) {
        String instanceToTerminateID = parameters.get("instanceid");
        if (serverApi.delete(instanceToTerminateID)) {
            return new SalsaConfigureResult(actionID, SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "Remove VM done !");
        } else {
            return new SalsaConfigureResult(actionID, SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 0, "Failed to delete VM: Instance: " + instanceToTerminateID + ". Action ID: " + actionID);
        }
    }

    public void listImages() {
        try {
            ImageApi imageApi = client.getImageApiForZone(region);
            FluentIterable<? extends Image> images = imageApi.listInDetail().concat();
            PrintWriter f0 = new PrintWriter(new FileWriter("/tmp/dsg_openstack_images.txt"));

            for (Image image : images) {
                System.out.println("\t" + image);
                f0.println(image);
            }
            f0.close();
        } catch (Exception e) {

        }
    }

    @Override
    public String getStatus(SalsaConfigureTask configInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "openstack";
    }

}
