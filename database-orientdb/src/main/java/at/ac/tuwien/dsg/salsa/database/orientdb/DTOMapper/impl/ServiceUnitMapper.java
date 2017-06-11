/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl;

import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.DTOMapperInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;

import at.ac.tuwien.dsg.salsa.database.orientdb.Utils;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.properties.ArtifactsWrapper;
import at.ac.tuwien.dsg.salsa.model.properties.CapabilitiesWrapper;

/**
 *
 * @author hungld
 */
public class ServiceUnitMapper implements DTOMapperInterface<ServiceUnit> {

    @Override
    public ServiceUnit fromODocument(ODocument doc) {
        ServiceUnit unit = new ServiceUnit();

        unit.setArtifacts(ArtifactsWrapper.fromJson(String.valueOf(doc.field("artifacts"))).getArtifacts());
        unit.setCapabilities(CapabilitiesWrapper.fromJson(String.valueOf(doc.field("capabilities"))).getCapabilities());
        unit.setHostedOn(String.valueOf(doc.field("hostedUnitName")));
        unit.setCloudServiceUuid(String.valueOf("cloudServiceUuid"));
        unit.setIdCounter(Integer.parseInt(String.valueOf(doc.field("idCounter"))));
        unit.setMax(Integer.parseInt(String.valueOf(doc.field("max"))));
        unit.setMin(Integer.parseInt(String.valueOf(doc.field("min"))));
        unit.setName(String.valueOf(doc.field("name")));
        unit.setProperties(Utils.jsonToMap(String.valueOf(doc.field("properties"))));
        unit.setReference(String.valueOf(doc.field("reference")));
        unit.setState(ConfigurationState.valueOf(String.valueOf(doc.field("state"))));
        unit.setTopologyUuid(String.valueOf("topologyUuid"));
        unit.setType(String.valueOf("type"));
        unit.setUuid(String.valueOf("uuid"));

        return unit;
    }

    @Override
    public ODocument toODocument(ServiceUnit object) {
        ODocument doc = new ODocument();
        doc.setClassName(ServiceUnit.class.getSimpleName());

        doc.field("artifacts", new ArtifactsWrapper(object.getArtifacts()).toJson());
        doc.field("capabilities", new CapabilitiesWrapper(object.getCapabilities()).toJson());
        doc.field("hostedUnitName", object.getHostedOn());
        doc.field("cloudServiceUuid", object.getCloudServiceUuid());
        doc.field("idCounter", object.getIdCounter());
        doc.field("max", object.getMax());
        doc.field("min", object.getMin());
        doc.field("name", object.getName());
        doc.field("properties", Utils.mapToJson(object.getProperties()));
        doc.field("reference", object.getReference());
        doc.field("state", object.getState().name());
        doc.field("topologyUuid", object.getTopologyUuid());
        doc.field("type", object.getType());
        doc.field("uuid", object.getUuid());

        return doc;
    }

}
