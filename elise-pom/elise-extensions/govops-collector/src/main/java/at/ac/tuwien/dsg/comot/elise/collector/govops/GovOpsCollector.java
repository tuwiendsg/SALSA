/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.comot.elise.collector.govops;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.UnitInstanceCollector;
import at.ac.tuwien.dsg.cloud.elise.model.generic.Capability;
import at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels.RestExecution;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.IDType;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IoT.GatewayInfo;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class GovOpsCollector extends UnitInstanceCollector {

    String govoptREST;

    public GovOpsCollector() {
        System.out.println("Start GovOps collector");
        Date date = new Date();
        try {
            FileUtils.writeStringToFile(new File("/tmp/govopsRunCheck.txt"), "GovOps run at:" + date.toString());
        } catch (IOException ex) {
            Logger.getLogger(GovOpsCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.govoptREST = readAdaptorConfig("endpoint");
        if (govoptREST==null || govoptREST.isEmpty()){
            govoptREST = "http://localhost:8080/APIManager";
        }
    }

    @Override
    public UnitInstance collectInstanceByID(String domainID) {
        System.out.println("Collect instance by ID : " + domainID);
        Set<UnitInstance> allInstances = collectAllInstance();
        if (allInstances != null && !allInstances.isEmpty()) {
            for (UnitInstance ins : allInstances) {
                System.out.println("Checking instance:" + ins.toJson());
                DomainEntity entity = DomainEntity.fromJson(ins.getDomainInfo());
                if (entity.getDomainID().equals(domainID)) {
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
//        Client orderClient = ClientBuilder.newClient();
//        WebTarget target = orderClient.target(this.govoptREST + "/governanceScope/globalScope");
//        GenericType<String> genericType = new GenericType<String>() {
//        };
//        String devicesJson = (String) target.request(new String[]{"application/json"}).get(genericType);

        String devicesJson = RestHandler.callRest(this.govoptREST + "/governanceScope/globalScope", RestHandler.HttpVerb.GET, null, null, "application/json");
        
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

        for (DeviceDTO d : devices) {
            UnitInstance u = new UnitInstance(d.getName(), ServiceCategory.Gateway);
            GatewayInfo gatewayInfo = new GatewayInfo();
            gatewayInfo.setIp(d.getIpAddress());
            gatewayInfo.setDomainID(d.getId());
            if (d.getMeta().get("location") != null) {
                gatewayInfo.setLocation(d.getMeta().get("location"));
            }
            u.hasExtra("GovOptID", d.getId());

            String idParam = d.getId().replace(".", "_");
            System.out.println("Query to: " + this.govoptREST + "/mapper/capabilities/list/" + idParam);
//            target = orderClient.target(this.govoptREST + "/mapper/capabilities/list/" + idParam);
//            
//            Invocation.Builder builder = target.request(new String[]{"application/json"});
//
//            Response res = target.request(new String[]{"application/json"}).get();
            
            String allCapaResponse = RestHandler.callRest(this.govoptREST + "/mapper/capabilities/list/" + idParam, RestHandler.HttpVerb.GET, null, null, "application/json");
            
            if (allCapaResponse!=null && !allCapaResponse.isEmpty()) {
                String capasStr = "{" + allCapaResponse.replace("}, ]", "} ]") + "}";
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
            u.setDomainInfo(gatewayInfo.toJson());
            units.add(u);
        }
        return units;
    }

    @Override
    public LocalIdentification identify(UnitInstance instance) {
        DomainEntity entity = DomainEntity.fromJson(instance.getDomainInfo());
        String unitID = entity.getDomainID();
        System.out.println("Get govops ID: " + unitID);
//        String unitID = instance.findFeatureByName("govops-device").getMetricValueByName("id").getValue().toString();
        String ip = unitID.split(":")[0].trim();
        String port = unitID.split(":")[1].trim();
        System.out.println("IP: "+ip+", PORT: " + port);
        LocalIdentification id = new LocalIdentification(ServiceCategory.Gateway, "rtGovOps");
        id.hasIdentification(IDType.IP_PORT.toString(), unitID);
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
