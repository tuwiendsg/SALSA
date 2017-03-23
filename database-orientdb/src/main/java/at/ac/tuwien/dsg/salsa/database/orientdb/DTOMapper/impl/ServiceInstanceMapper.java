/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl;

import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.DTOMapperInterface;
import at.ac.tuwien.dsg.salsa.database.orientdb.Utils;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;
import com.orientechnologies.orient.core.record.impl.ODocument;
import javax.rmi.CORBA.Util;

/**
 *
 * @author hungld
 */
public class ServiceInstanceMapper implements DTOMapperInterface<ServiceInstance> {

    @Override
    public ServiceInstance fromODocument(ODocument doc) {

        ServiceInstance instance = new ServiceInstance();
        instance.setContext(Utils.jsonToMap(String.valueOf(doc.field("context"))));
        instance.setHostedInstanceIndex(Integer.valueOf(String.valueOf(doc.field("hostInstanceIndex"))));
        instance.setIdentification(GlobalIdentification.fromJson(String.valueOf(doc.field("identification"))));
        instance.setIndex(Integer.valueOf(String.valueOf("index")));
        instance.setServiceUnitUuid(String.valueOf(doc.field("serviceUnitUuid")));
        instance.setState(ConfigurationState.valueOf(String.valueOf(doc.field("state"))));
        instance.setUuid(String.valueOf("uuid"));
        instance.setCloudServiceUuid(String.valueOf("cloudServiceUuid"));
        instance.setTopologyUuid(String.valueOf("topologyUuid"));

        return instance;
    }

    @Override
    public ODocument toODocument(ServiceInstance object) {

        ODocument doc = new ODocument();

        doc.setClassName(ServiceInstance.class.getSimpleName());

        doc.field("context", Utils.mapToJson(object.getContext()));
        doc.field("hostInstanceIndex", object.getHostedInstanceIndex());
        doc.field("identification", object.getIdentification().toJson());
        doc.field("index", object.getIndex());
        doc.field("serviceUnitUuid", object.getServiceUnitUuid());
        doc.field("state", object.getState().name());
        doc.field("uuid", object.getUuid());
        doc.field("cloudServiceUuid", object.getCloudServiceUuid());
        doc.field("topologyUuid", object.getTopologyUuid());

        return doc;
    }

}
