package at.ac.tuwien.dsg.cloud.salsa.salsa_common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.Metric;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.melaconcepts.MetricValue;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.model.CloudProvider;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.model.Resource;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.cloudinformation.model.ServiceUnit;

/**
 * Hello world!
 *
 */
public class GenerateCloudsDescriptionTest {

    public static void main(String[] args) throws Exception {

        List<CloudProvider> cloudProviders = new ArrayList<CloudProvider>();
        
        cloudProviders.add(FlexiantGeneration());
        cloudProviders.add(DSGCloudGeneration());
        cloudProviders.add(StratuslabGeneration());       
        
        String path = "/home/hungld/test/cloudproviders/";
        for (CloudProvider provider : cloudProviders) {
			provider.exportXML(new File(path+provider.getName()));
		}        

    }
    
    
    private static CloudProvider FlexiantGeneration(){
    	//dsg cloud description
        
        CloudProvider cloudProvider = new CloudProvider("fco@flexiant", CloudProvider.Type.IAAS);
            
            //m1.small
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "4GB/3CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(3));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(4000));
                    utility.addResourceProperty(resource);

                }
          
            }

            //m1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "4GB/4CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(4));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(4000));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.micro
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "2GB/2CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(2000));
                    utility.addResourceProperty(resource);
                }

            }

            //m1.large
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "4GB/2CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(4000));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "1GB/1CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(1000));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.2xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "2GB/1CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(2000));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "0.5GB/2CPU");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(500));
                    utility.addResourceProperty(resource);
                }
            }        
        return cloudProvider;
    	
    }
    
    
    
    private static CloudProvider DSGCloudGeneration(){
    	//dsg cloud description        
            CloudProvider cloudProvider = new CloudProvider("dsg@openstack", CloudProvider.Type.IAAS);
            
            //m1.tiny
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.tiny");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(512));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(0));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(0));
                    utility.addResourceProperty(resource);
                }

//
//            //vm quality: memory optimized
//            {
//                Quality q = new Quality("memoryOptimized");
//                q.addProperty(new Metric("IO/s", "number"), new MetricValue(1000000));
//                utility.addQualityProperty(q);
//            }



//
//            //utility cost
//            {
//                CostFunction c = new CostFunction("cost", CostFunction.Type.PERIODIC);
//
//                CostElement ce = new CostElement(new Metric("hourlyUsage", "hour"));
//                ce.setName("vmHourlYSUageCostElement");
//                ce.addCostInterval(new MetricValue(1), 0.12);
//
//                c.addCostElement(ce);
//
//                utility.addCostFunction(c);
//            }
//
//            //utility quality
//            {
//                Quality q = new Quality("availability");
//                q.addProperty(new Metric("availability", "%"), new MetricValue(99.9));
//                utility.addQualityProperty(q);
//
//            }
            }

            //m1.small
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.small");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(1920));
                    utility.addResourceProperty(resource);

                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(20));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.medium");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(3750));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.micro
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.micro");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(960));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(0));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.large
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.large");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(4));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(7680));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(80));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.xlarge");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(8));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(15360));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(160));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.2xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.2xlarge");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(4));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(7680));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(30));
                    utility.addResourceProperty(resource);
                }
            }

            //m1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m2.medium");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(3));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(5760));
                    utility.addResourceProperty(resource);
                }

                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("EphemeralDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }


                //vm resources: RootDisk
                {
                    Resource resource = new Resource("RootDisk");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(40));
                    utility.addResourceProperty(resource);
                }
            }
        return cloudProvider;
    }
    
    
    private static CloudProvider StratuslabGeneration(){
    	 //==========================================================================================
        //lal-stratus cloud description
        
            CloudProvider cloudProvider = new CloudProvider("lal@stratuslab", CloudProvider.Type.IAAS);
            
         
            //m1.small
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.small");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(1536));
                    utility.addResourceProperty(resource);

                }
                
            }

            //m1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.medium");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(3));
                    utility.addResourceProperty(resource);
                }
                
            }

            //t1.micro
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "t1.micro");
                cloudProvider.addServiceUnit(utility);

                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(512));
                    utility.addResourceProperty(resource);
                }

            }

            //m1.large
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.large");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(6144));
                    utility.addResourceProperty(resource);
                }

                //vm resources: Swap
                {
                    Resource resource = new Resource("Swap");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(6144));
                    utility.addResourceProperty(resource);
                }

            }

            //m1.xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "m1.xlarge");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(4));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(8192));
                    utility.addResourceProperty(resource);
                }

                //vm resources: Swap
                {
                    Resource resource = new Resource("Swap");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(8192));
                    utility.addResourceProperty(resource);
                }
             
            }

            //c1.medium
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "c1.medium");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(1536));
                    utility.addResourceProperty(resource);
                }

              //vm resources: Swap
                {
                    Resource resource = new Resource("Swap");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(1536));
                    utility.addResourceProperty(resource);
                }
            }

            //c1.xlarge
            {
                ServiceUnit utility = new ServiceUnit("VirtualInfrastructure", "VM", "c1.xlarge");
                cloudProvider.addServiceUnit(utility);


                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(4));
                    utility.addResourceProperty(resource);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(6144));
                    utility.addResourceProperty(resource);
                }

              //vm resources: Swap
                {
                    Resource resource = new Resource("Swap");
                    resource.addProperty(new Metric("size", "MB"), new MetricValue(6144));
                    utility.addResourceProperty(resource);
                }
            }
        return cloudProvider;
    	
    }
    
    
    
    
    
    
}
