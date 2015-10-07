/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IoT.GatewayInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class GovOpsCollector extends UnitInstanceCollector {

    String govoptREST = "http://128.130.172.199:8080/APIManager";

    public GovOpsCollector() {
        this.govoptREST = readAdaptorConfig("endpoint");
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        System.out.println("Collect instance by ID : " + domainID);
        Set<UnitInstance> allInstances = collectAllInstance();
        if (allInstances != null && !allInstances.isEmpty()) {
            for (UnitInstance ins : allInstances) {
                System.out.println("Checking instance:" + ins.toJson());
                DomainEntity entity = DomainEntity.fromJson(ins.getDomainInfo());
                if (entity.getDomainID().equals(domainID)){
                    System.out.println("Found instance: " + ins.getId());
                    return ins;
                }
            }
        }
        System.out.println("Did not find any instance with domain ID: " + domainID);
        return null;
    }

    @Override
    public Set<UnitInstance> collectAllInstance() {
        Client orderClient = ClientBuilder.newClient();
        WebTarget target = orderClient.target(this.govoptREST + "/governanceScope/globalScope");
        GenericType<String> genericType = new GenericType<String>() {
        };
        String devicesJson = (String) target.request(new String[]{"application/json"}).get(genericType);

        System.out.println("Get data from GovOps in Json: " + devicesJson);

        ObjectMapper mapper = new ObjectMapper();
        List<DeviceDTO> devices;
        try {
            devices = ((DevicesDTO) mapper.readValue(devicesJson, DevicesDTO.class)).getDevices();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Set<UnitInstance> units = new HashSet<>();
        GatewayInfo gatewayInfo = new GatewayInfo();

        for (DeviceDTO d : devices) {
            UnitInstance u = new UnitInstance(d.getName(), ServiceCategory.Gateway);
            gatewayInfo.setIp(d.getIpAddress());
            gatewayInfo.setDomainID(d.getId());
            if (d.getMeta().get("location") != null) {
                gatewayInfo.setLocation(d.getMeta().get("location"));
            }
            u.hasExtra("GovOptID", d.getId());            

            String idParam = d.getId().replace(".", "_");
            System.out.println("Query to: " + this.govoptREST + "/mapper/capabilities/list/" + idParam);
            target = orderClient.target(this.govoptREST + "/mapper/capabilities/list/" + idParam);
            Invocation.Builder builder = target.request(new String[]{"application/json"});

            Response res = target.request(new String[]{"application/json"}).get();
            if (res.getStatus() < 400) {
                String capasStr = "{" + ((String) res.readEntity(String.class)).replace("}, ]", "} ]") + "}";
                System.out.println("Parsing capabilities json: " + capasStr);
                try {
                    DeviceCapabilities capas = (DeviceCapabilities) mapper.readValue(capasStr, DeviceCapabilities.class);
                    for (DeviceCapability c : capas.getCapabilities()) {
                        // only get the capability with c and end with .sh. This is the conventional from GovOps
                        if (c.getCapability().startsWith("c") && c.getCapability().endsWith(".sh")) {
                            // remove the c and .sh from the name of the script
                            String capaName = c.getCapability().substring(1, c.getCapability().length() - 3);
                            u.hasCapability(new Capability(capaName, Capability.ExecutionMethod.REST, new RestExecution(this.govoptREST + "/invoke/" + d.getId() + "/" + c.getCapability(), RestExecution.RestMethod.GET, "")).executedBy("rtGovOps"));
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            units.add(u);
        }
        return units;
    }

    @Override
    public LocalIdentification identify(UnitInstance instance) {
        DomainEntity entity = DomainEntity.fromJson(instance.getDomainInfo());
        String unitID = entity.getDomainID();        
//        String unitID = instance.findFeatureByName("govops-device").getMetricValueByName("id").getValue().toString();
        String ip = unitID.split(":")[0].trim();
        String port = unitID.split(":")[1].trim();
        LocalIdentification id = new LocalIdentification(ServiceCategory.Gateway, "rtGovOps");
        id.hasIdentification("rtGovOpsID", unitID);
//        id.hasIdentification("rtGovOpsID", ip+":"+port);
//        id.hasIdentification("ip", ip);
//        id.hasIdentification("port", port);
        return id;
    }

    @Override
    public String getName() {
        return "rtGovOps-collector";
    }

}
