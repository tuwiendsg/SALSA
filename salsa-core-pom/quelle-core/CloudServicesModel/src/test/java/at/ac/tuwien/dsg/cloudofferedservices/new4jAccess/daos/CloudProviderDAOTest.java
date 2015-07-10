/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloudofferedservices.new4jAccess.daos;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostElement;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CostFunction;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.ElasticityCapability;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Quality;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.Resource;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudOfferedService;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author daniel-tuwien
 */
public class CloudProviderDAOTest extends TestCase {

    public CloudProviderDAOTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of matchServiceUnit method, of class RequirementsMatchingEngine.
     */
    public void testEcosystemDescription() throws IOException {

        List<CloudProvider> cloudProviders = new ArrayList<CloudProvider>();
//        

        //==========================================================================================
        //amazon cloud description
        //dsg cloud description
        {
            CloudProvider cloudProvider = new CloudProvider("Amazon EC2", CloudProvider.Type.IAAS);
            cloudProviders.add(cloudProvider);

//            //Monitoring
//            {
//                ServiceUnit utility = new ServiceUnit("Management", "Monitoring", "Monitoring");
//                cloudProvider.addServiceUnit(utility);
//
//                //utility quality
//                {
//                    Quality q = new Quality("MetricsCount");
//                    q.addProperty(new Metric("monitoredMetrics", "count"), new MetricValue("5"));
//                    q.addProperty(new Metric("monitoredFreq", "h"), new MetricValue("1"));
//
//                    utility.addQualityProperty(q);
//                }
//
//            }
            //EBS
            {
                CloudOfferedService utility = new CloudOfferedService("IaaS", "Storage", "EBS");
                cloudProvider.addCloudOfferedService(utility);

                //utility quality
                Quality stdQuality = new Quality("I/O Performance");
                stdQuality.addProperty(new Metric("IOperformance", "IOPS"), new MetricValue("100"));
                utility.addQualityProperty(stdQuality);

                //utility quality
                Quality highQuality = new Quality("I/O Performance");
                highQuality.addProperty(new Metric("IOperformance", "IOPS"), new MetricValue("4000"));
                utility.addQualityProperty(highQuality);

                {
                    Quality q = new Quality("I/O Performance");
                    ElasticityCapability characteristic = new ElasticityCapability("StoragePerformance");
                    characteristic.setPhase(ElasticityCapability.Phase.INSTANTIATION_TIME);
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(q, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    utility.addElasticityCapability(characteristic);
                }

//                $0.10 per GB-month of provisioned storage
//$0.10 per 1 million I/O requests
//Amazon EBS Provisioned IOPS volumes
//$0.125 per GB-month of provisioned storage
//$0.10 per provisioned IOPS-month
//                {
//                    CostFunction costFunctionForStdPerformance = new CostFunction("CostForStdPerformance");
//                    {
//                        //currently Cost is cost unit agnostic?
//                        CostElement costPerGB = new CostElement("StorageCost", new Metric("storageSize", "GB", Metric.MetricType.RESOURCE), CostElement.Type.USAGE);
//                        costPerGB.addCostInterval(new MetricValue(1), 0.1);
//                        costFunctionForStdPerformance.addCostElement(costPerGB);
//                    }
//
//                    {
//                        CostElement costPerIO = new CostElement("I/OCost", new Metric("I/Os", "million", Metric.MetricType.RESOURCE), CostElement.Type.USAGE);
//                        costPerIO.addCostInterval(new MetricValue(1), 0.1);
//                        costFunctionForStdPerformance.addCostElement(costPerIO);
//                    }
//                    costFunctionForStdPerformance.addUtilityAppliedInConjunctionWith(stdQuality);
//                    utility.addCostFunction(costFunctionForStdPerformance);
//                }
////
//                {
//                    CostFunction costFunctionForMaxPerformance = new CostFunction("CostForMaxPerformance");
//                    {
//                        //currently Cost is cost unit agnostic?
//                        CostElement costPerGB = new CostElement("StorageCost", new Metric("storageSize", "GB", Metric.MetricType.RESOURCE), CostElement.Type.USAGE);
//                        costPerGB.addCostInterval(new MetricValue(1), 0.125);
//                        costFunctionForMaxPerformance.addCostElement(costPerGB);
//                    }
//
//                    {
//                        CostElement costPerIO = new CostElement("I/OCost", new Metric("I/Os", "million", Metric.MetricType.RESOURCE), CostElement.Type.USAGE);
//                        costPerIO.addCostInterval(new MetricValue(1), 0.1);
//                        costFunctionForMaxPerformance.addCostElement(costPerIO);
//                    }
//                    costFunctionForMaxPerformance.addUtilityAppliedInConjunctionWith(highQuality);
//                    utility.addCostFunction(costFunctionForMaxPerformance);
//                }
            }

//
            //m1.large
            {
                CloudOfferedService utility = new CloudOfferedService("IaaS", "VM", "m1.large");
                cloudProvider.addCloudOfferedService(utility);

                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("Architecture", "type"), new MetricValue("x64"));
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //vm resources: computing
                {
                    Resource resource = new Resource("Computing");
                    resource.addProperty(new Metric("Architecture", "type"), new MetricValue("x86"));
                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
                    utility.addResourceProperty(resource);
                }

                //utility elasticity charact for resource" lets the utility choose between diff resource values
                {
                    Resource resource = new Resource("Computing");
                    ElasticityCapability characteristic = new ElasticityCapability("Architecture");
                    characteristic.setPhase(ElasticityCapability.Phase.INSTANTIATION_TIME);

                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(resource, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
//                        characteristic.addOption(new MetricValue("x86"));
//                        characteristic.addOption(new MetricValue("x64"));
                    utility.addElasticityCapability(characteristic);
                }

                //vm resources: memory
                {
                    Resource resource = new Resource("Memory");
                    resource.addProperty(new Metric("memorySize", "GB"), new MetricValue(7.5));
                    utility.addResourceProperty(resource);
                }

//                //vm resources: EphemeralDisk
                {
                    Resource resource = new Resource("InstanceStorage");
                    resource.addProperty(new Metric("size", "GB"), new MetricValue(420));
                    resource.addProperty(new Metric("disks", "number"), new MetricValue(2));
                    utility.addResourceProperty(resource);
                }

                //utility quality
                {
                    Quality q = new Quality("NetworkPerformance");
                    q.addProperty(new Metric("performance", "rank"), new MetricValue("Moderate"));
                    utility.addQualityProperty(q);

                }

                {
                    Quality q = new Quality("ComputingPerformance");
                    q.addProperty(new Metric("ECU", "number"), new MetricValue(4));
                    utility.addQualityProperty(q);

                }

                {
                    CloudOfferedService sUtility = new CloudOfferedService("IaaS", "Storage", "EBS");
                    //utility elasticity charact for resource" lets the utility choose between diff resource values
                    {
                        ElasticityCapability characteristic = new ElasticityCapability("StorageElCapability");

                        characteristic.setPhase(ElasticityCapability.Phase.INSTANTIATION_TIME);
                        characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(sUtility, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                        utility.addElasticityCapability(characteristic);
                    }
                }
//
                //add as Resource ServiceUnit the reservationScheme
                //then I can add different cost as associated with diff ServiceUnit Reqs.
                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("OnDemand"));
                    utility.addResourceProperty(reservationScheme);

                    //add cost per association with this reservationScheme
                    CostFunction onDemandCost = new CostFunction("OnDemandCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.24);
                        onDemandCost.addCostElement(hourlyCost);
                    }
                    onDemandCost.addAppliedIfServiceInstanceUses(reservationScheme);
                    utility.addCostFunction(onDemandCost);
                }

                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Spot"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction spotCost = new CostFunction("SpotCost");

                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.026);
                        spotCost.addCostElement(hourlyCost);
                    }
                    spotCost.addAppliedIfServiceInstanceUses(reservationScheme);

                    utility.addCostFunction(spotCost);
                }

                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved1YearLightUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _1YearReservedCost = new CostFunction("1YearLightUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 243.0);
                        _1YearReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.13);
                        _1YearReservedCost.addCostElement(hourlyCost);
                    }
                    _1YearReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);

                    utility.addCostFunction(_1YearReservedCost);

                }
                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved1YearMediumUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _1YearReservedCost = new CostFunction("1YearMediumUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 554.0);
                        _1YearReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.13);
                        _1YearReservedCost.addCostElement(hourlyCost);
                    }
                    _1YearReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);
                }
                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved1YearHeavyUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _1YearReservedCost = new CostFunction("1YearHeavyUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 676.0);
                        _1YearReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.056);
                        _1YearReservedCost.addCostElement(hourlyCost);
                    }
                    _1YearReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);

                    utility.addCostFunction(_1YearReservedCost);
                }

                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved2YearsLightUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _2YearsReservedCost = new CostFunction("2YearsLightUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 384.0);
                        _2YearsReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.108);
                        _2YearsReservedCost.addCostElement(hourlyCost);
                    }
                    _2YearsReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);
                    utility.addCostFunction(_2YearsReservedCost);
                }
                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved2YearsMediumUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _2YearsReservedCost = new CostFunction("2YearsMediumUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 860.0);
                        _2YearsReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.064);
                        _2YearsReservedCost.addCostElement(hourlyCost);
                    }
                    _2YearsReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);
                    utility.addCostFunction(_2YearsReservedCost);
                }
                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    reservationScheme.addProperty(new Metric("Reservation", "type"), new MetricValue("Reserved2YearsHeavyUtilization"));
                    utility.addResourceProperty(reservationScheme);

                    CostFunction _2YearsReservedCost = new CostFunction("2YearsHeavyUtilizationCost");
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement upfrontCost = new CostElement("UpfrontCost", new Metric("OneTimePay", "value", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        upfrontCost.addBillingInterval(new MetricValue(1), 1028.0);
                        _2YearsReservedCost.addCostElement(upfrontCost);
                    }
                    {
                        //currently Cost is cost unit agnostic?
                        CostElement hourlyCost = new CostElement("HourlyCost", new Metric("hourlyUsage", "hour", Metric.MetricType.RESOURCE), CostElement.Type.PERIODIC);
                        hourlyCost.addBillingInterval(new MetricValue(1), 0.046);
                        _2YearsReservedCost.addCostElement(hourlyCost);
                    }
                    _2YearsReservedCost.addAppliedIfServiceInstanceUses(reservationScheme);
                    utility.addCostFunction(_2YearsReservedCost);

                }

                {
                    Resource reservationScheme = new Resource("ReservationScheme");
                    ElasticityCapability characteristic = new ElasticityCapability("ReservationSchemeElasticity");
                    characteristic.setPhase(ElasticityCapability.Phase.INSTANTIATION_TIME);
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(reservationScheme, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
//                        characteristic.setCapabilityMetric(new Metric("storage", "type"));
//                        characteristic.addOption(new MetricValue("EBS"));
//                        characteristic.addOption(new MetricValue("Standard"));
                    utility.addElasticityCapability(characteristic);
                }

                {

                    ElasticityCapability characteristic = new ElasticityCapability("CostElasticity");
                    characteristic.setPhase(ElasticityCapability.Phase.INSTANTIATION_TIME);

                    CostFunction _2YearsReservedCostHeavy = new CostFunction("2YearsHEavyUtilizationCost");
                    CostFunction _2YearsReservedCostMedium = new CostFunction("2YearsMediumUtilizationCost");
                    CostFunction _2YearsReservedCostLight = new CostFunction("2YearsLightUtilizationCost");
                    CostFunction _1YearReservedCostHeavy = new CostFunction("1YearHeavyUtilizationCost");
                    CostFunction _1YearReservedCostMedium = new CostFunction("1YearMediumUtilizationCost");
                    CostFunction _1YearReservedCostLight = new CostFunction("1YearLightUtilizationCost");
                    CostFunction spotCost = new CostFunction("SpotCost");
                    CostFunction onDemandCost = new CostFunction("OnDemandCost");

                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_2YearsReservedCostHeavy, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_2YearsReservedCostMedium, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_2YearsReservedCostLight, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_1YearReservedCostHeavy, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_1YearReservedCostMedium, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(_1YearReservedCostMedium, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(spotCost, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));
                    characteristic.addCapabilityDependency(new ElasticityCapability.Dependency(onDemandCost, ElasticityCapability.Type.OPTIONAL_ASSOCIATION));

                    utility.addElasticityCapability(characteristic);
                }

            }

//            //m1.large
//            {
//                ServiceUnit utility = new ServiceUnit("IaaS", "VM", "m1.xsmall");
//                cloudProvider.addServiceUnit(utility);
//
//
//                //vm resources: computing
//                {
//                    Resource resource = new Resource("Computing");
//                    resource.addProperty(new Metric("Architecture", "type"), new MetricValue("x86"));
//                    resource.addProperty(new Metric("VCPU", "number"), new MetricValue(1));
//                    utility.addResourceProperty(resource);
//                }
//
//
//
//                //vm resources: memory
//                {
//                    Resource resource = new Resource("Memory");
//                    resource.addProperty(new Metric("memorySize", "GB"), new MetricValue(4));
//                    utility.addResourceProperty(resource);
//                }
//
//
//                //utility quality
//                {
//                    Quality q = new Quality("NetworkPerformance");
//                    q.addProperty(new Metric("performance", "rank"), new MetricValue("Low"));
//                    utility.addQualityProperty(q);
//
//                }
//
//                {
//                    Quality q = new Quality("ComputingPerformance");
//                    q.addProperty(new Metric("ECU", "number"), new MetricValue(1));
//                    utility.addQualityProperty(q);
//                }
//
//
//            }
        }

    }
}
