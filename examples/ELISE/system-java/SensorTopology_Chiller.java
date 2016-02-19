/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.icomot.examples.test;

import java.util.Map;

import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilder;
import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilderImpl;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.MiscArtifact;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.ServiceArtifact;
import at.ac.tuwien.dsg.comot.common.model.Capability;
import at.ac.tuwien.dsg.comot.common.model.CloudService;
import static at.ac.tuwien.dsg.comot.common.model.CloudService.ServiceTemplate;
import static at.ac.tuwien.dsg.comot.common.model.CommonOperatingSystemSpecification.OpenstackSmall;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.HostedOnRelation;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.ConnectToRelation;
import at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit;
import static at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit.OperatingSystemUnit;
import at.ac.tuwien.dsg.comot.common.model.Requirement;
import static at.ac.tuwien.dsg.comot.common.model.ServiceTopology.ServiceTopology;
import at.ac.tuwien.dsg.comot.common.model.ServiceTopology;
import at.ac.tuwien.dsg.comot.common.model.ServiceUnit;
import at.ac.tuwien.dsg.comot.common.model.SoftwareNode;
import static at.ac.tuwien.dsg.comot.common.model.SoftwareNode.SingleSoftwareUnit;
import at.ac.tuwien.dsg.icomot.iCOMOTOrchestrator;
import at.ac.tuwien.dsg.icomot.util.ProcessArgs;
import at.ac.tuwien.dsg.icomot.util.ProcessArgs.Arg;

/**
 * This example simulates sensors and gateways, which send data to the Cloud IOT Platform deployed by the ElasticIoTPlatform.java example file
 *
 * @author http://dsg.tuwien.ac.at
 */
public class SensorTopology_Chiller {

    public static void main(String[] args) {

        String chillerSensorRepo = "http://128.130.172.215/iCOMOTTutorial/files/IoTSensorData/chiller";
        
        // QueueUnit reference
        ServiceUnit MqttQueueVM = OperatingSystemUnit("MqttQueueVM")
                .providedBy(OpenstackSmall())
                .andReference("IoTSensors/MqttQueueVM");

        ServiceUnit QueueUnit = SoftwareNode.SingleSoftwareUnit("QueueUnit")
                .exposes(Capability.Variable("brokerIp_Capability"))
                .andReference("IoTSensors/QueueUnit");

        ServiceTopology gatewayTopology = ServiceTopology("QueueServiceTopology")
                .withServiceUnits(MqttQueueVM, QueueUnit);

        // evaporator_fouling_topology
        OperatingSystemUnit evaporator_fouling_sensors_VM = OperatingSystemUnit("evaporator_fouling_sensors_VM")
                .providedBy(OpenstackSmall()
                        .withBaseImage("7ac2cc53-2301-40d7-a030-910d72f552ff") // this image includes docker, faster spin up
                );

        
        ServiceUnit evaporator_fouling_ch2a_exv_position = SingleSoftwareUnit("evaporator_fouling_ch2a_exv_position")
                .requires(Requirement.Variable("brokerIp_Requirement_evaporator_fouling_ch2a_exv_position"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_evaporator_fouling_ch2a_exv_position.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz"))           
                .withMaxColocatedInstances(1);

        ServiceUnit evaporator_fouling_chw_supply_temp = SingleSoftwareUnit("evaporator_fouling_chw_supply_temp")
                .requires(Requirement.Variable("brokerIp_Requirement_evaporator_fouling_chw_supply_temp"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_evaporator_fouling_chw_supply_temp.sh"))                           
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz"))  
                .withMaxColocatedInstances(1);
        ServiceUnit evaporator_fouling_fcu_ff1_set_point = SingleSoftwareUnit("evaporator_fouling_fcu_ff1_set_point")
                .requires(Requirement.Variable("brokerIp_Requirement_evaporator_fouling_fcu_ff1_set_point"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_evaporator_fouling_fcu_ff1_set_point.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz"))  
                .withMaxColocatedInstances(1);
        ServiceUnit evaporator_fouling_fcu_ff1_space_temp = SingleSoftwareUnit("evaporator_fouling_fcu_ff1_space_temp")
                .requires(Requirement.Variable("brokerIp_Requirement_evaporator_fouling_fcu_ff1_space_temp"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_evaporator_fouling_fcu_ff1_space_temp.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz"))  
                .withMaxColocatedInstances(1);
        
        ServiceTopology evaporator_fouling_topology = ServiceTopology("evaporator_fouling_topology")
                .withServiceUnits(evaporator_fouling_sensors_VM, evaporator_fouling_ch2a_exv_position, evaporator_fouling_chw_supply_temp, evaporator_fouling_fcu_ff1_set_point, evaporator_fouling_fcu_ff1_space_temp);

        // condenser_rule_topology
        OperatingSystemUnit condenser_rule_sensors_VM = OperatingSystemUnit("condenser_rule_sensors_VM")
                .providedBy(OpenstackSmall()
                        .withBaseImage("7ac2cc53-2301-40d7-a030-910d72f552ff") // this image includes docker, faster spin up
                );

        ServiceUnit condenser_rule_difference_oat_and_con_temp = SingleSoftwareUnit("condenser_rule_difference_oat_and_con_temp")
                .requires(Requirement.Variable("brokerIp_Requirement_condenser_rule_difference_oat_and_con_temp"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_condenser_rule_difference_oat_and_con_temp.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz"))  
                .withMaxColocatedInstances(1);

        ServiceUnit condenser_rule_motor_status = SingleSoftwareUnit("condenser_rule_motor_status")
                .requires(Requirement.Variable("brokerIp_Requirement_condenser_rule_motor_status"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_condenser_rule_motor_status.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);

        ServiceUnit condenser_rule_oat = SingleSoftwareUnit("condenser_rule_oat")
                .requires(Requirement.Variable("brokerIp_Requirement_condenser_rule_oat"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_condenser_rule_oat.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        
        ServiceTopology condenser_rule_topology = ServiceTopology("condenser_rule_topology")
                .withServiceUnits(condenser_rule_sensors_VM, condenser_rule_difference_oat_and_con_temp, condenser_rule_motor_status, condenser_rule_oat);

        // low_suction_pressure_ch3_topology
        OperatingSystemUnit low_suction_pressure_ch3_VM = OperatingSystemUnit("low_suction_pressure_ch3_VM")
                .providedBy(OpenstackSmall()
                        .withBaseImage("7ac2cc53-2301-40d7-a030-910d72f552ff") // this image includes docker, faster spin up
                );

        ServiceUnit ch3_cktA_compressor_suction_superheat_temp = SingleSoftwareUnit("ch3_cktA_compressor_suction_superheat_temp")
                .requires(Requirement.Variable("brokerIp_Requirement_ch3_cktA_compressor_suction_superheat_temp"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_low_suction_pressure_ch3_ch3_cktA_compressor_suction_superheat_temp.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        ServiceUnit ch3_cktA_exv_position = SingleSoftwareUnit("ch3_cktA_exv_position")
                .requires(Requirement.Variable("brokerIp_Requirement_ch3_cktA_exv_position"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_low_suction_pressure_ch3_ch3_cktA_exv_position.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        ServiceUnit ch3_cktA_percent_total_capacity = SingleSoftwareUnit("ch3_cktA_percent_total_capacity")
                .requires(Requirement.Variable("brokerIp_Requirement_ch3_cktA_percent_total_capacity"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_low_suction_pressure_ch3_ch3_cktA_percent_total_capacity.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        ServiceUnit ch3_cktA_suction_pressure = SingleSoftwareUnit("ch3_cktA_suction_pressure")
                .requires(Requirement.Variable("brokerIp_Requirement_ch3_cktA_suction_pressure"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_low_suction_pressure_ch3_ch3_cktA_suction_pressure.sh"))
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        ServiceUnit cmn_chws_temp = SingleSoftwareUnit("cmn_chws_temp")
                .requires(Requirement.Variable("brokerIp_Requirement_cmn_chws_temp"))
                .deployedBy(ServiceArtifact(chillerSensorRepo + "runSensor_low_suction_pressure_ch3_ch3_cmn_chws_temp.sh"))
                                                                 
                .deployedBy(MiscArtifact(chillerSensorRepo+"sensor.tar.gz")) 
                .withMaxColocatedInstances(1);
        ServiceTopology low_suction_pressure_ch3_topology = ServiceTopology("low_suction_pressure_ch3_topology")
                .withServiceUnits(low_suction_pressure_ch3_VM, ch3_cktA_compressor_suction_superheat_temp,
                        ch3_cktA_exv_position, ch3_cktA_percent_total_capacity, ch3_cktA_suction_pressure, cmn_chws_temp);

//        DockerUnit gatewayDocker = DockerUnit("gatewayDocker")
//                .providedBy(DockerDefault())
//                .deployedBy(DockerFileArtifact("dockerFileArtifact", salsaRepo + "Dockerfile-UB"),
//                        MiscArtifact("starter.sh", salsaRepo + "starter_ubuntu.sh"),
//                        MiscArtifact("achieveArtifact", salsaRepo + "rtGovOps-agents.tar.gz"));
        CloudService serviceTemplate = ServiceTemplate("ChillerSensors_OpenStack")
                .consistsOfTopologies(gatewayTopology)
                    .andRelationships(HostedOnRelation("QueueUnitOnMqttQueueVM")
                        .from(QueueUnit)
                        .to(MqttQueueVM))
                .consistsOfTopologies(evaporator_fouling_topology, low_suction_pressure_ch3_topology, condenser_rule_topology)                
                    .andRelationships(
                            // evaporator_fouling
                     HostedOnRelation("evaporator_fouling_ch2a_exv_position_on_VM")
                        .from(evaporator_fouling_ch2a_exv_position)
                        .to(evaporator_fouling_sensors_VM)
                    ,HostedOnRelation("evaporator_fouling_chw_supply_temp_on_VM")
                        .from(evaporator_fouling_chw_supply_temp)
                        .to(evaporator_fouling_sensors_VM)
                    ,HostedOnRelation("evaporator_fouling_fcu_ff1_set_point_on_VM")
                        .from(evaporator_fouling_fcu_ff1_set_point)
                        .to(evaporator_fouling_sensors_VM)
                    ,HostedOnRelation("evaporator_fouling_fcu_ff1_space_temp_on_VM")
                        .from(evaporator_fouling_fcu_ff1_space_temp)
                        .to(evaporator_fouling_sensors_VM)
                            // condenser_rule
                    ,HostedOnRelation("condenser_rule_difference_oat_and_con_temp_on_VM")
                        .from(condenser_rule_difference_oat_and_con_temp)
                        .to(condenser_rule_sensors_VM)      
                    ,HostedOnRelation("condenser_rule_motor_status_on_VM")
                        .from(condenser_rule_motor_status)
                        .to(condenser_rule_sensors_VM)   
                    ,HostedOnRelation("condenser_rule_oat_on_VM")
                        .from(condenser_rule_oat)
                        .to(condenser_rule_sensors_VM)   
                            //low_suction_pressure_ch3_topology
                    ,HostedOnRelation("ch3_cktA_compressor_suction_superheat_temp_on_VM")
                        .from(ch3_cktA_compressor_suction_superheat_temp)
                        .to(low_suction_pressure_ch3_VM)   
                    ,HostedOnRelation("ch3_cktA_exv_position_on_VM")
                        .from(ch3_cktA_exv_position)
                        .to(low_suction_pressure_ch3_VM) 
                    ,HostedOnRelation("ch3_cktA_percent_total_capacity_on_VM")
                        .from(ch3_cktA_percent_total_capacity)
                        .to(low_suction_pressure_ch3_VM) 
                    ,HostedOnRelation("ch3_cktA_suction_pressure_on_VM")
                        .from(ch3_cktA_suction_pressure)
                        .to(low_suction_pressure_ch3_VM) 
                    ,HostedOnRelation("cmn_chws_temp_on_VM")
                        .from(cmn_chws_temp)
                        .to(low_suction_pressure_ch3_VM))
                         
                            // connecto relationship, connect all sensors to the queue
                .andRelationships(
                            // evaporator_fouling
                     ConnectToRelation("mqtt_broker_condenser_rule_difference_oat_and_con_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(evaporator_fouling_ch2a_exv_position.getContext().get("brokerIp_Requirement_evaporator_fouling_ch2a_exv_position"))
                    ,ConnectToRelation("mqtt_broker_condenser_rule_difference_oat_and_con_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(evaporator_fouling_chw_supply_temp.getContext().get("brokerIp_Requirement_evaporator_fouling_chw_supply_temp"))
                    ,ConnectToRelation("mqtt_broker_evaporator_fouling_fcu_ff1_set_point")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(evaporator_fouling_fcu_ff1_set_point.getContext().get("brokerIp_Requirement_evaporator_fouling_fcu_ff1_set_point"))
                    ,ConnectToRelation("mqtt_broker_evaporator_fouling_fcu_ff1_space_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(evaporator_fouling_fcu_ff1_space_temp.getContext().get("brokerIp_Requirement_evaporator_fouling_fcu_ff1_space_temp"))
                            // condenser_rule_topology
                    ,ConnectToRelation("mqtt_broker_condenser_rule_difference_oat_and_con_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(condenser_rule_difference_oat_and_con_temp.getContext().get("brokerIp_Requirement_condenser_rule_difference_oat_and_con_temp"))
                    ,ConnectToRelation("mqtt_broker_condenser_rule_motor_status")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(condenser_rule_motor_status.getContext().get("brokerIp_Requirement_condenser_rule_motor_status"))
                    ,ConnectToRelation("mqtt_broker_condenser_rule_oat")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(condenser_rule_oat.getContext().get("brokerIp_Requirement_condenser_rule_oat"))
                        
                            // low_suction_pressure_ch3_topology
                    ,ConnectToRelation("mqtt_broker_ch3_cktA_compressor_suction_superheat_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(ch3_cktA_compressor_suction_superheat_temp.getContext().get("brokerIp_Requirement_ch3_cktA_compressor_suction_superheat_temp"))
                    ,ConnectToRelation("mqtt_broker_ch3_cktA_exv_position")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(ch3_cktA_exv_position.getContext().get("brokerIp_Requirement_ch3_cktA_exv_position"))
                    ,ConnectToRelation("mqtt_broker_ch3_cktA_percent_total_capacity")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(ch3_cktA_percent_total_capacity.getContext().get("brokerIp_Requirement_ch3_cktA_percent_total_capacity"))                            
                    ,ConnectToRelation("mqtt_broker_ch3_cktA_suction_pressure")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(ch3_cktA_suction_pressure.getContext().get("brokerIp_Requirement_ch3_cktA_suction_pressure"))
                    ,ConnectToRelation("mqtt_broker_cmn_chws_temp")
                        .from(QueueUnit.getContext().get("brokerIp_Capability"))
                        .to(cmn_chws_temp.getContext().get("brokerIp_Requirement_cmn_chws_temp"))                    
                    );
        
                // note: the ID of connectto relationship for Sensors must be started with "mqtt", the sensor code is hard-coded to read this pattern.
                

        ToscaDescriptionBuilder toscaBuilder = new ToscaDescriptionBuilderImpl();
        String tosca = toscaBuilder.toXml(serviceTemplate);
        System.out.println(tosca);

        iCOMOTOrchestrator orchestrator = new iCOMOTOrchestrator("128.130.172.215");
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
       
       
       orchestrator.deploy(serviceTemplate);
    }
}

