package at.ac.tuwien.dsg.icomot.examples.test_HUNG_on_workspace;

import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilder;
import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilderImpl;
import java.util.Map;

import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.MiscArtifact;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.SingleScriptArtifact;
import static at.ac.tuwien.dsg.comot.common.model.BASHAction.BASHAction;
import at.ac.tuwien.dsg.comot.common.model.Capability;
import static at.ac.tuwien.dsg.comot.common.model.CommonOperatingSystemSpecification.OpenstackMicro;
import static at.ac.tuwien.dsg.comot.common.model.CommonOperatingSystemSpecification.OpenstackSmall;
import at.ac.tuwien.dsg.comot.common.model.Constraint;
import at.ac.tuwien.dsg.comot.common.model.Constraint.Metric;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.ConnectToRelation;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.HostedOnRelation;
import at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit;
import static at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit.OperatingSystemUnit;
import at.ac.tuwien.dsg.comot.common.model.Requirement;
import at.ac.tuwien.dsg.comot.common.model.CloudService;
import static at.ac.tuwien.dsg.comot.common.model.CloudService.ServiceTemplate;
import at.ac.tuwien.dsg.comot.common.model.ElasticityCapability;
import at.ac.tuwien.dsg.comot.common.model.LifecyclePhase;
import at.ac.tuwien.dsg.comot.common.model.ServiceTopology;
import static at.ac.tuwien.dsg.comot.common.model.ServiceTopology.ServiceTopology;
import at.ac.tuwien.dsg.comot.common.model.ServiceUnit;
import static at.ac.tuwien.dsg.comot.common.model.SoftwareNode.SingleSoftwareUnit;
import static at.ac.tuwien.dsg.comot.common.model.Strategy.Strategy;
import at.ac.tuwien.dsg.icomot.iCOMOTOrchestrator;
import at.ac.tuwien.dsg.icomot.util.ProcessArgs;
import at.ac.tuwien.dsg.icomot.util.ProcessArgs.Arg;

/**
 * This example deploys an elastic IOT platform running in the cloud
 *
 * @author http://dsg.tuwien.ac.at
 */
public class ElasticIoTPlatform {

    public static void main(String[] args) {
        //specify service units in terms of software

        String platformRepo = "http://128.130.172.215/iCOMOTTutorial/files/ElasticIoTCloudPlatform/";
        String miscRepo = "http://128.130.172.215/iCOMOTTutorial/files/Misc/";

        //need to specify details of VM and operating system to deploy the software servide units on
        OperatingSystemUnit dataControllerVM = OperatingSystemUnit("DataControllerUnitVM")
                .providedBy(OpenstackSmall()
                        //OS image having JDK and Ganglia preinstalled, for faster deploy time
                        .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c")
                );

        OperatingSystemUnit dataNodeVM = OperatingSystemUnit("DataNodeUnitVM")
                .providedBy(OpenstackMicro()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        //finally, we define Vm types for event processing
        OperatingSystemUnit loadbalancerVM = OperatingSystemUnit("LoadBalancerUnitVM")
                .providedBy(OpenstackSmall()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        OperatingSystemUnit eventProcessingVM = OperatingSystemUnit("EventProcessingUnitVM")
                .providedBy(OpenstackSmall()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        OperatingSystemUnit localProcessingVM = OperatingSystemUnit("LocalProcessingUnitVM")
                .providedBy(OpenstackSmall()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        OperatingSystemUnit mqttQueueVM = OperatingSystemUnit("MqttQueueVM")
                .providedBy(OpenstackSmall()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        OperatingSystemUnit momVM = OperatingSystemUnit("MoMVM")
                .providedBy(OpenstackSmall()
                 .withBaseImage("a82e054f-4f01-49f9-bc4c-77a98045739c"));

        //start with Data End, and first with Data Controller
        ServiceUnit dataControllerUnit = SingleSoftwareUnit("DataControllerUnit")
                //software artifacts needed for unit deployment   = software artifact archive and script to deploy Cassandra
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployCassandraSeed.sh"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/ElasticCassandraSetup-1.0.tar.gz"))
                //data controller exposed its IP 
                .exposes(Capability.Variable("DataController_IP_information"));

        ElasticityCapability dataNodeUnitScaleIn = ElasticityCapability.ScaleIn();
        ElasticityCapability dataNodeUnitScaleOut = ElasticityCapability.ScaleOut();

        //specify data node
        ServiceUnit dataNodeUnit = SingleSoftwareUnit("DataNodeUnit")
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployCassandraNode.sh"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/ElasticCassandraSetup-1.0.tar.gz"))
                //data node MUST KNOW the IP of cassandra seed, to connect to it and join data cluster
                .requires(Requirement.Variable("DataController_IP_Data_Node_Req").withName("requiringDataNodeIP"))
                //.provides(dataNodeUnitScaleIn, dataNodeUnitScaleOut)
                //express elasticity strategy: Scale IN Data Node when cpu usage < 40%
                .controlledBy(Strategy("DN_ST1")
                        .when(Constraint.MetricConstraint("DN_ST1_CO1", new Metric("cpuUsage", "%")).lessThan("40"))
                        .enforce(dataNodeUnitScaleIn)
                )
                .controlledBy(Strategy("DN_ST2")
                        .when(Constraint.MetricConstraint("DN_ST2_CO1", new Metric("cpuUsage", "%")).greaterThan("80"))
                        .enforce(dataNodeUnitScaleOut)
                )
                .withLifecycleAction(LifecyclePhase.STOP, BASHAction("sudo service joinRing stop"));

        //add the service units belonging to the event processing topology
        ServiceUnit momUnit = SingleSoftwareUnit("MOMUnit")
                //load balancer must provide IP
                .exposes(Capability.Variable("MOM_IP_information"))
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployMoM.sh"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/DaaSQueue-1.0.tar.gz"));

        ElasticityCapability eventProcessingUnitScaleIn = ElasticityCapability.ScaleIn();
        ElasticityCapability eventProcessingUnitScaleOut = ElasticityCapability.ScaleOut();

        //add the service units belonging to the event processing topology
        ServiceUnit eventProcessingUnit = SingleSoftwareUnit("EventProcessingUnit")
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployEventProcessing.sh"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/DaaS-1.0.tar.gz"))
                //event processing must register in Load Balancer, so it needs the IP
                .requires(Requirement.Variable("EventProcessingUnit_LoadBalancer_IP_Req"))
                //event processing also needs to querry the Data Controller to access data
                .requires(Requirement.Variable("EventProcessingUnit_DataController_IP_Req"))
                .requires(Requirement.Variable("EventProcessingUnit_MOM_IP_Req"))
                .provides(eventProcessingUnitScaleIn, eventProcessingUnitScaleOut)
                //scale IN if throughput < 200 and responseTime < 200
                .controlledBy(Strategy("EP_ST1")
                        .when(Constraint.MetricConstraint("EP_ST1_CO1", new Metric("responseTime", "ms")).lessThan("100"))
                        .and(Constraint.MetricConstraint("EP_ST1_CO2", new Metric("avgThroughput", "operations/s")).lessThan("200"))
                        .enforce(eventProcessingUnitScaleIn)
                )
                .controlledBy(Strategy("EP_ST2")
                        .when(Constraint.MetricConstraint("EP_ST2_CO1", new Metric("responseTime", "ms")).greaterThan("100"))
                        .and(Constraint.MetricConstraint("EP_ST2_CO2", new Metric("avgThroughput", "operations/s")).greaterThan("200"))
                        .enforce(eventProcessingUnitScaleOut)
                )
                .withLifecycleAction(LifecyclePhase.STOP, BASHAction("sudo service event-processing stop"));

        //add the service units belonging to the event processing topology
        ServiceUnit loadbalancerUnit = SingleSoftwareUnit("LoadBalancerUnit")
                //load balancer must provide IP
                .exposes(Capability.Variable("LoadBalancer_IP_information"))
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployLoadBalancer.sh"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/HAProxySetup-1.0.tar.gz"));

        ServiceUnit mqttUnit = SingleSoftwareUnit("QueueUnit")
                //load balancer must provide IP
                .exposes(Capability.Variable("brokerIp_Capability"))
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/run_mqtt_broker.sh"));

        ElasticityCapability localProcessingUnitScaleIn = ElasticityCapability.ScaleIn().withPrimitiveOperations("Salsa.scaleIn");
        ElasticityCapability localProcessingUnitScaleOut = ElasticityCapability.ScaleOut().withPrimitiveOperations("Salsa.scaleOut");

        ServiceUnit localProcessingUnit = SingleSoftwareUnit("LocalProcessingUnit")
                //load balancer must provide IP
                .requires(Requirement.Variable("brokerIp_Requirement"))
                .requires(Requirement.Variable("loadBalancerIp_Requirement"))
                .provides(localProcessingUnitScaleIn, localProcessingUnitScaleOut)
                .deployedBy(SingleScriptArtifact(platformRepo + "scripts/OpenStack/deployLocalAnalysis.sh"))
                .deployedBy(MiscArtifact(miscRepo + "artifacts/jre-7-linux-x64.tar.gz"))
                .deployedBy(MiscArtifact(platformRepo + "artifacts/LocalDataAnalysis.tar.gz"));

        //Describe a Data End service topology containing the previous 2 software service units
        ServiceTopology dataEndTopology = ServiceTopology("DataEndTopology")
                .withServiceUnits(dataControllerUnit, dataNodeUnit //add also OS units to topology
                        , dataControllerVM, dataNodeVM
                );

        //specify constraints on the data topology
        //thus, the CPU usage of all Service Unit instances of the data end Topology must be below 80%
        dataEndTopology.controlledBy(Strategy("EP_ST3")
                .when(Constraint.MetricConstraint("DET_CO1", new Metric("cpuUsage", "%")).lessThan("80"))
                .enforce(eventProcessingUnitScaleOut)
        );

        //define event processing unit topology
        ServiceTopology eventProcessingTopology = ServiceTopology("EventProcessingTopology")
                .withServiceUnits(loadbalancerUnit, eventProcessingUnit, momUnit //add vm types to topology
                        , loadbalancerVM, eventProcessingVM, momVM
                );

        ServiceTopology localProcessinTopology = ServiceTopology("Gateway")
                .withServiceUnits(mqttQueueVM, mqttUnit, localProcessingUnit, localProcessingVM
                );

        localProcessingUnit.
                controlledBy(Strategy("LPT_ST1").when(Constraint.MetricConstraint("LPT_ST1_CO1", new Metric("avgBufferSize", "#")).lessThan("50"))
                        .enforce(localProcessingUnitScaleIn));
        localProcessingUnit.
                controlledBy(Strategy("LPT_ST2").when(Constraint.MetricConstraint("LPT_ST2_CO1", new Metric("avgBufferSize", "#")).greaterThan("50"))
                        .enforce(localProcessingUnitScaleOut));

        //describe the service template which will hold more topologies
        CloudService serviceTemplate = ServiceTemplate("ElasticIoTPlatform")
                .consistsOfTopologies(dataEndTopology)
                .consistsOfTopologies(eventProcessingTopology)
                .consistsOfTopologies(localProcessinTopology)
                //defining CONNECT_TO and HOSTED_ON relationships
                .andRelationships(
                        //Data Controller IP send to Data Node
                        ConnectToRelation("dataNodeToDataController")
                        .from(dataControllerUnit.getContext().get("DataController_IP_information"))
                        .to(dataNodeUnit.getContext().get("DataController_IP_Data_Node_Req")) //specify which software unit goes to which VM
                        ,
                        //event processing gets IP from load balancer
                        ConnectToRelation("eventProcessingToLoadBalancer")
                        .from(loadbalancerUnit.getContext().get("LoadBalancer_IP_information"))
                        .to(eventProcessingUnit.getContext().get("EventProcessingUnit_LoadBalancer_IP_Req")) //specify which software unit goes to which VM
                        ,
                        //event processing gets IP from data controller
                        ConnectToRelation("eventProcessingToDataController")
                        .from(dataControllerUnit.getContext().get("DataController_IP_information"))
                        .to(eventProcessingUnit.getContext().get("EventProcessingUnit_DataController_IP_Req")) //specify which software unit goes to which VM
                        ,
                        ConnectToRelation("eventProcessingToMOM")
                        .from(momUnit.getContext().get("MOM_IP_information"))
                        .to(eventProcessingUnit.getContext().get("EventProcessingUnit_MOM_IP_Req")) //specify which software unit goes to which VM
                        ,
                        ConnectToRelation("mqtt_broker")
                        .from(mqttUnit.getContext().get("brokerIp_Capability"))
                        .to(localProcessingUnit.getContext().get("brokerIp_Requirement")) //specify which software unit goes to which VM
                        ,
                        ConnectToRelation("load_balancer")
                        .from(loadbalancerUnit.getContext().get("LoadBalancer_IP_information"))
                        .to(localProcessingUnit.getContext().get("loadBalancerIp_Requirement")) //specify which software unit goes to which VM
                        ,
                        HostedOnRelation("dataControllerToVM")
                        .from(dataControllerUnit)
                        .to(dataControllerVM),
                        HostedOnRelation("dataNodeToVM")
                        .from(dataNodeUnit)
                        .to(dataNodeVM) //add hosted on relatinos
                        , HostedOnRelation("loadbalancerToVM")
                        .from(loadbalancerUnit)
                        .to(loadbalancerVM),
                        HostedOnRelation("eventProcessingToVM")
                        .from(eventProcessingUnit)
                        .to(eventProcessingVM),
                        HostedOnRelation("momToVM")
                        .from(momUnit)
                        .to(momVM),
                        HostedOnRelation("localProcessingToVM")
                        .from(localProcessingUnit)
                        .to(localProcessingVM),
                        HostedOnRelation("mqttToVM")
                        .from(mqttUnit)
                        .to(mqttQueueVM)
                )
                .withDefaultMetrics();

        iCOMOTOrchestrator orchestrator = new iCOMOTOrchestrator("localhost");
        
     // added to make it easier to run as jar from cmd line
     		{
     			Map<Arg, String> argsMap = ProcessArgs.processArgs(args);
     			for (Arg key : argsMap.keySet()) {
     				switch (key) {
     				case ORCHESTRATOR_IP:
     					orchestrator.withIP(argsMap.get(key));
     					break;
     				case SALSA_IP:
     					orchestrator.withSalsaIP(argsMap.get(key));
     					break;
     				case SALSA_PORT:
     					orchestrator.withSalsaPort(Integer.parseInt(argsMap
     							.get(key)));
     					break;
     				case rSYBL_IP:
     					orchestrator.withRsyblIP(argsMap.get(key));
     					break;
     				case rSYBL_PORT:
     					orchestrator.withRsyblPort(Integer.parseInt(argsMap
     							.get(key)));
     					break;
     				case GovOps_IP:
     					orchestrator.withGovOpsIP(argsMap.get(key));
     					break;
     				case GovOps_PORT:
     					orchestrator.withGovOpsPort(Integer.parseInt(argsMap
     							.get(key)));
     					break;
     				}
     			}
     		}
                
                orchestrator.withGovOpsIP("128.130.172.199").withGovOpsPort(8080)
                        .withSalsaIP("128.130.172.215").withSalsaPort(8888)
                        .withRsyblIP("128.130.172.215").withRsyblPort(8080);                        
                
                ToscaDescriptionBuilder tosca = new ToscaDescriptionBuilderImpl();                
                System.out.println(tosca.toXml(serviceTemplate));
        //orchestrator.deploy(serviceTemplate);
        
        //only to deploy
        //orchestrator.deploy(serviceTemplate);

        //for updating anything
        //orchestrator.controlExisting(serviceTemplate);
    }
}

