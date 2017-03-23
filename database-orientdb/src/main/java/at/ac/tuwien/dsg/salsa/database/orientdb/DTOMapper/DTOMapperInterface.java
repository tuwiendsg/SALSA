/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author hungld
 * @param <T> The class to be converted
 * 
 * Note: the toOdocument must persist the field name uuid in order for later queries
 */
public interface DTOMapperInterface<T> {

    T fromODocument(ODocument doc);

    ODocument toODocument(T object);

}
