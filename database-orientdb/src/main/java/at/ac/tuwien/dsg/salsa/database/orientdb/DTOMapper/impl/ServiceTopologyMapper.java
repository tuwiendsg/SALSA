/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl;

import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.DTOMapperInterface;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author hungld
 */
public class ServiceTopologyMapper implements DTOMapperInterface<ServiceTopology> {

    @Override
    public ServiceTopology fromODocument(ODocument doc) {

        ServiceTopology topo = new ServiceTopology();
        topo.setCloudServiceUuid(String.valueOf(doc.field("cloudServiceUUID")));
        topo.setName(String.valueOf(doc.field("name")));
        topo.setState(ConfigurationState.valueOf(String.valueOf(doc.field("state"))));
        topo.setUuid(String.valueOf(doc.field("uuid")));

        return topo;
    }

    @Override
    public ODocument toODocument(ServiceTopology object) {

        ODocument doc = new ODocument();

        doc.setClassName(ServiceTopology.class.getSimpleName());
        doc.field("cloudServiceUUID", object.getCloudServiceUuid());
        doc.field("name", object.getName());
        doc.field("state", object.getState().name());
        doc.field("uuid", object.getUuid());

        return doc;
    }

}
