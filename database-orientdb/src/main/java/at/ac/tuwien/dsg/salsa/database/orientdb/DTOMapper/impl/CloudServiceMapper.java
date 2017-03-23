/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl;

import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.DTOMapperInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvents;

/**
 *
 * @author hungld
 */
public class CloudServiceMapper implements DTOMapperInterface<CloudService> {

    @Override
    public CloudService fromODocument(ODocument doc) {
        CloudService service = new CloudService();
        service.setName(String.valueOf(doc.field("name")));
        service.setState(ConfigurationState.valueOf(String.valueOf(doc.field("state"))));
        service.setEvents(SalsaEvents.fromJson(String.valueOf(doc.field("events"))));
        service.setUuid(String.valueOf(doc.field("uuid")));

        return service;
    }

    @Override
    public ODocument toODocument(CloudService object) {
        ODocument doc = new ODocument();
        doc.setClassName(CloudService.class.getSimpleName());

        doc.field("name", object.getName());
        doc.field("state", object.getState().name());
        doc.field("events", object.getEvents().toJson());
        doc.field("uuid", object.getUuid());

        return doc;
    }

}
