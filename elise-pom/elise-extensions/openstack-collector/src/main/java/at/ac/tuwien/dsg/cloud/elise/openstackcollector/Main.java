/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.openstackcollector;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo.State;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import com.google.common.collect.Multimap;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.rest.RestContext;

/**
 *
 * @author Duc-Hung LE
 */
public class Main extends UnitInstanceCollector {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Main.class);
    NovaApi client;
    ServerApi serverApi;
    VolumeApi volumeApi;
    String keyName;
    final String region = "myregion";
    
    @Override
    public Set<UnitInstance> collectAllInstance() {
        init();
        Set<UnitInstance> instances = new HashSet<>();
        System.out.println("Servers in myregion");
        for (Server server : this.serverApi.listInDetail().concat()) {
            UnitInstance instance = openstackServerToVMInfo(server);
            if (instance != null) {
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        init();
        return openstackServerToVMInfo(this.serverApi.get(domainID));
    }

    private UnitInstance openstackServerToVMInfo(Server server) {
        if (server == null) {
            return null;
        }
        logger.debug("Found server: " + server.getName());
        VirtualMachineInfo vmInfo = new VirtualMachineInfo(readAdaptorConfig(OpenStackParameterStrings.END_POINT.getString()), server.getId(), server.getName());
        vmInfo.setBaseImageID(server.getImage().getId());
        vmInfo.setBaseImageName(server.getImage().getName());
        vmInfo.setConfigDrive(server.getConfigDrive());
        vmInfo.setFlavorID(server.getFlavor().getId());
        vmInfo.setFlavorName(server.getFlavor().getName());
        vmInfo.setHostId(server.getHostId());
        vmInfo.setInstanceId(server.getUuid());
        vmInfo.setKeyname(server.getKeyName());
        vmInfo.setStatus(server.getStatus().value());
        vmInfo.setTenantId(server.getTenantId());
        vmInfo.setUpdated(server.getUpdated().toString());
        vmInfo.setUserID(server.getUserId());
        vmInfo.setCurrentState(keyName);

        Multimap<String, Address> addresses = server.getAddresses();

        Collection<Address> ips = addresses.get("private");
        for (Address a : ips) {
            String ip = a.getAddr();
            logger.debug("FOUND AN ADDRESS: " + ip);

            InetAddress inet;
            try {
                inet = InetAddress.getByName(ip);

                if (inet.isSiteLocalAddress()) {
                    logger.debug("FOUND AN LOCAL ADDRESS: " + ip);
                    vmInfo.setPrivateIp(ip);
                } else {
                    logger.debug("FOUND AN PUBLIC ADDRESS: " + ip);
                    vmInfo.setPublicIp(ip);
                }
            } catch (java.net.UnknownHostException ex) {
                logger.error("Error when parsing IP addresses: {}" + ex.getMessage());
                ex.printStackTrace();
            }
        }
        UnitInstance instance = new UnitInstance(server.getName(), ServiceCategory.VirtualMachine);
        instance.setDomainInfo(vmInfo.toJson());
        return instance;
    }

    private State convertOpenstackStatusToVMDomainState(Status status) {
        if (status == null) {
            return State.unknown;
        }
        switch (status) {
            case BUILD:
            case REBUILD:
                return State.spawning;
            case REBOOT:
            case RESIZE:
                return State.configuring;
            case ACTIVE:
                return State.running;
            case PAUSED:
            case SUSPENDED:
            case STOPPED:
                return State.stopped;
            case ERROR:
                return State.error;            
            default:
                return State.unknown;
        }
    }

    @Override
    public LocalIdentification identify(UnitInstance paramUnitInstance) {
        String domainInfo = paramUnitInstance.getDomainInfo();
        VirtualMachineInfo vmInfo = (VirtualMachineInfo) VirtualMachineInfo.fromJson(domainInfo);
        LocalIdentification local = new LocalIdentification(ServiceCategory.VirtualMachine, this.getName());
        local.hasIdentification("ip", vmInfo.getPrivateIp());
        return local;
    }

    @Override
    public String getName() {
        return "OpenstackCollector";
    }

    private void init() {
        System.out.println("THIS IS ON THE SCREEN !!!");
        logger.info("Reading configuration file ...");

        String tenant = readAdaptorConfig(OpenStackParameterStrings.TENANT.getString());
        String username = readAdaptorConfig(OpenStackParameterStrings.USERNAME.getString());
        String password = readAdaptorConfig(OpenStackParameterStrings.PASSWORD.getString());
        String endpoint = readAdaptorConfig(OpenStackParameterStrings.KEYSTONE_ENDPOINT.getString());
        logger.debug("Tenant:   " + tenant);
        logger.debug("Username: " + username);
        logger.debug("Password: " + password);
        logger.debug("Endpoint: " + endpoint);

        ComputeServiceContext context = (ComputeServiceContext) ContextBuilder.newBuilder("openstack-nova").credentials(tenant + ":" + username, password).endpoint(endpoint).buildView(ComputeServiceContext.class);

        this.client = ((NovaApi) ((RestContext) context.unwrap(NovaApiMetadata.CONTEXT_TOKEN)).getApi());
        this.serverApi = this.client.getServerApiForZone("myregion");
        logger.info("Done initiation !");
    }

}
