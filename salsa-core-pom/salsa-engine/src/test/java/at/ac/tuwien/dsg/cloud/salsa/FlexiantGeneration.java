package at.ac.tuwien.dsg.cloud.salsa;


import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlexiantGeneration {

    static final Logger log = LoggerFactory.getLogger(FlexiantGeneration.class);

    public FlexiantGeneration() {
    }

    public String generateFlexiantResource() {
        CloudProvider flexiant = new CloudProvider("Flexiant", "IaaS");
        
        {
            CloudOfferedService unit = new CloudOfferedService("IaaS", "VM", "CloudStorage");
            
            // TODO: finish this 
        }
        return "";
    }
}
