/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.icomot.examples.test_ELISE;

import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilder;
import at.ac.tuwien.dsg.comot.api.ToscaDescriptionBuilderImpl;
import at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.MiscArtifact;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.MetaArtifact;
import static at.ac.tuwien.dsg.comot.common.model.ArtifactTemplate.SingleScriptArtifact;
import at.ac.tuwien.dsg.comot.common.model.Capability;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.ConnectToRelation;
import static at.ac.tuwien.dsg.comot.common.model.EntityRelationship.HostedOnRelation;
import at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit;
import at.ac.tuwien.dsg.comot.common.model.Requirement;
import at.ac.tuwien.dsg.comot.common.model.CloudService;
import static at.ac.tuwien.dsg.comot.common.model.CloudService.ServiceTemplate;
import static at.ac.tuwien.dsg.comot.common.model.CommonOperatingSystemSpecification.DockerDefault;
import static at.ac.tuwien.dsg.comot.common.model.CommonOperatingSystemSpecification.LocalDocker;
import static at.ac.tuwien.dsg.comot.common.model.DockerUnit.DockerUnit;
import at.ac.tuwien.dsg.comot.common.model.DockerUnit;

import static at.ac.tuwien.dsg.comot.common.model.OperatingSystemUnit.OperatingSystemUnit;
import at.ac.tuwien.dsg.comot.common.model.ServiceTopology;
import static at.ac.tuwien.dsg.comot.common.model.ServiceTopology.ServiceTopology;
import at.ac.tuwien.dsg.comot.common.model.ServiceUnit;
import static at.ac.tuwien.dsg.comot.common.model.SoftwareNode.SingleSoftwareUnit;

/**
 *
 * @author hungld
 */
public class SimpleSensorOnDocker {

    public static void main(String[] args) {
        OperatingSystemUnit personalMachine = OperatingSystemUnit("PersonalMachine")
                .providedBy(LocalDocker()
                );

        ServiceUnit sensorUnit = SingleSoftwareUnit("sensorUnit")
                .requires(Requirement.Variable("brokerIp_Requirement"))                
                .deployedBy(SingleScriptArtifact("https://dl.dropboxusercontent.com/s/b7aeky5osdqzwth/runSensor_gps1279_location.sh"))
                .deployedBy(MiscArtifact("https://dl.dropboxusercontent.com/s/cmy6h6gbpyb4jib/sensor.tar.gz"))
                .deployedBy(MetaArtifact("https://dl.dropboxusercontent.com/s/w69krddj4qmqc2m/sensor.meta"))
                .withMaxColocatedInstances(1);
        
        DockerUnit sensorDocker = DockerUnit("SensorDocker")
                .providedBy(DockerDefault().addSoftwarePackage("ganglia-monitor gmetad")
                );

        ServiceUnit mqttUnit = SingleSoftwareUnit("QueueUnit")
                //load balancer must provide IP
                .exposes(Capability.Variable("brokerIp_Capability"))
                .deployedBy(SingleScriptArtifact("https://dl.dropboxusercontent.com/s/dxfm1q1r3f464p7/run_mqtt_broker.sh"));

        DockerUnit mqttDocker = DockerUnit("MqttDocker")
                .providedBy(DockerDefault().addSoftwarePackage("ganglia-monitor gmetad")
                );

        ServiceTopology sensorTopology = ServiceTopology("SensorTopology")
                .withServiceUnits(sensorUnit, mqttUnit)
                .withServiceUnits(sensorDocker, mqttDocker, personalMachine);

        CloudService serviceTemplate = ServiceTemplate("IoTSensors")
                .consistsOfTopologies(sensorTopology)
                .andRelationships(HostedOnRelation("SensorUnitOnDocker")
                        .from(sensorUnit)
                        .to(sensorDocker))
                .andRelationships(HostedOnRelation("sensorDockerOnVM")
                        .from(sensorDocker)
                        .to(personalMachine))
                .andRelationships(HostedOnRelation("mqttDockerOnVM")
                        .from(mqttUnit)
                        .to(mqttDocker))                
                .andRelationships(HostedOnRelation("MqttUnitOnDocker")
                        .from(mqttDocker)
                        .to(personalMachine))
                // note: the ID of connectto relationship for Sensors must be started with "mqtt", the sensor code is hard-coded to read this pattern.
                .andRelationships(ConnectToRelation("mqtt_broker")
                        .from(mqttUnit.getContext().get("brokerIp_Capability"))
                        .to(sensorUnit.getContext().get("brokerIp_Requirement")));

        ToscaDescriptionBuilder toscaBuilder = new ToscaDescriptionBuilderImpl();
        String tosca = toscaBuilder.toXml(serviceTemplate);
        System.out.println(tosca);

        iCOMOTOrchestrator orchestrator = new iCOMOTOrchestrator("localhost");
        orchestrator.deploy(serviceTemplate);
    }
}

