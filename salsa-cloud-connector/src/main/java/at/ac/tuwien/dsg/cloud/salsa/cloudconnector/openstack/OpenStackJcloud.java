package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.openstack;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.VMStates;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;

public class OpenStackJcloud implements CloudInterface, Cloneable {

	//private final NovaApi novaApi;
    //private final Set<String> zones;
    Logger LOGGER;
    NovaApi client;
    ServerApi serverApi;
    VolumeApi volumeApi;

    String keyName;

    final String region = "myregion";
    Map<String, String> mapFlavorName = new HashMap<>();

    public OpenStackJcloud(Logger logger, String endpoint, String tenant, String username, String password, String keyName) {
        //logger.info(Configuration.getCloudAPIType()+" "+Configuration.getCloudUser()+" "+Configuration.getCloudPassword()+" "+Configuration.getCloudAPIEndpoint());
        this.LOGGER = logger;
        this.keyName = keyName;
        ComputeServiceContext context = ContextBuilder.newBuilder("openstack-nova")
                .credentials(tenant + ":" + username, password)
                .endpoint(endpoint)
                // .modules(modules)
                //.buildApi(NovaApi.class);
                .buildView(ComputeServiceContext.class);

        client = (NovaApi) context.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();

        serverApi = client.getServerApiForZone(region);

        for (Resource flavor : client.getFlavorApiForZone(region).list().concat()) {
            // System.out.println( flavor.getId()+" "+flavor.getName());
            mapFlavorName.put(flavor.getName(), flavor.getId());
        }

    }

    @Override
    public String launchInstance(String instanceName, String imageId, List<String> securityGroups,
            String sshKeyName, String userData, String instType,
            int minInst, int maxInst) throws ServiceDeployerException {
        LOGGER.debug("Jclouds Openstack Connector for node name: " + instanceName + ", imageId: " + imageId + ", instanceType: " + instType + "sshKeyName: " + sshKeyName);
        CreateServerOptions createNodeOptions = new CreateServerOptions();

        createNodeOptions.userData(userData.getBytes());
        createNodeOptions.keyPairName(this.keyName);

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
                    return serverCreated.getId();
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }
        return serverCreated.getId();
    }

    @Override
    public InstanceDescription getInstanceDescriptionByID(String instanceID) {
        Server server = serverApi.get(instanceID);
        Multimap<String, Address> map = server.getAddresses();
        List<Address> PA = (List<Address>) map.get("private");
        String privateIp = "";

        if (!PA.isEmpty()) {
            privateIp = PA.get(0).getAddr();
        }
        InstanceDescription des = new InstanceDescription(instanceID, privateIp, "");

        Status status = server.getStatus();
        switch (status) {
            case ACTIVE:
                des.setState(VMStates.Running);
                break;
            case BUILD:
                des.setState(VMStates.Prolog);
                break;
            case ERROR:
                des.setState(VMStates.Failed);
                break;
            case STOPPED:
                des.setState(VMStates.Terminated);
                break;
            case UNKNOWN:
                des.setState(VMStates.Unknown);
                break;
            default:
                des.setState(VMStates.Unknown);
                break;
        }
        return des;

    }

    @Override
    public void removeInstance(String instanceToTerminateID)
            throws ServiceDeployerException {
        serverApi.delete(instanceToTerminateID);
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

    public void listServers() {
        for (Server server : serverApi.listInDetail().concat()) {
            System.out.println("  " + server);
        }
    }

    public void printServerInfo(String id) {
        System.out.println("IPv4: " + serverApi.get(id).getAccessIPv4());
        System.out.println("IPv6: " + serverApi.get(id).getAccessIPv6());
        System.out.println("hostid: " + serverApi.get(id).getHostId());
        System.out.println("status: " + serverApi.get(id).getStatus());
        System.out.println("IPv4: " + serverApi.get(id).getAddresses());
        Multimap<String, Address> map = serverApi.get(id).getAddresses();
        List<Address> PA = (List<Address>) map.get("private");
        PA.get(0).getAddr();
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

    public void close() throws IOException {
        Closeables.close(client, true);
    }
}
