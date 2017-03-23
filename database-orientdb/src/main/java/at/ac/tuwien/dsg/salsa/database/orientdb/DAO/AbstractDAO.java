/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.DTOMapperInterface;
import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.MapperFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 * @param <T> The type of object to be persisted in DB
 */
public class AbstractDAO<T> {

    DTOMapperInterface<T> mapper;
    String className;
    static Logger logger = LoggerFactory.getLogger("SALSA");

    public AbstractDAO(Class clazz) {
        mapper = MapperFactory.getMapper(clazz);
        this.className = clazz.getSimpleName();
        if (mapper == null) {
            logger.error("No mapper for class " + className + " is found. Error!");
        }
        // if class name is not existed, create one
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        try {
            if (!db.getMetadata().getSchema().existsClass(className)) {
                logger.debug("Class: " + className + " does not existed, now create it...");
                db.getMetadata().getSchema().createClass(className);

                db.getMetadata().getSchema().getClass(className).createProperty("uuid", OType.STRING);
                db.getMetadata().getSchema().getClass(className).createIndex("uuidIndex_" + className, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "uuid");

                if (!db.getMetadata().getSchema().existsClass(className)) {
                    logger.debug("Cannot create class: " + className + ", an error of persistent could happen later!");
                } else {
                    logger.debug("Class " + className + " is created sucessfully !");
                }
            }
        } finally {
            manager.closeConnection();
        }
    }

    public ODocument save(T object) {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        try {
            ODocument odoc = mapper.toODocument(object);
            String uuid = odoc.field("uuid");
            ODocument existed = null;

            // Search for exist record
            if (db.getMetadata().getSchema().existsClass(className)) {
                String query1 = "SELECT * FROM " + className + " WHERE uuid = '" + uuid + "'";
                List<ODocument> existed_items = db.query(new OSQLSynchQuery<ODocument>(query1));
                logger.debug("Query: " + query1 + ". Result: " + existed_items.size());
                if (!existed_items.isEmpty()) {
                    existed = existed_items.get(0);
                }
            } else {
                logger.debug("No class exist: " + className);
            }
            ODocument result;
            // merge or create new
            if (uuid != null && existed != null) {
                existed.merge(odoc, true, false);
                logger.trace("Merging and saving odoc object: " + existed.toJSON());
                result = db.save(existed);
            } else {
                logger.trace("Saving odoc object: " + odoc.toJSON());
                result = db.save(odoc);
            }
            logger.debug("Save object done: " + uuid);
            logger.trace("Save done: " + result.toJSON());
            return result;
        } finally {
            manager.closeConnection();
        }
    }

    public List<ODocument> saveAll(Collection<T> objects) {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        List<ODocument> result = new ArrayList<>();

        try {
            String taskID = UUID.randomUUID().toString();
            Long startTime = (new Date()).getTime();
            logger.debug("Prepare to save " + objects.size() + " items -- " + taskID);
            for (T obj : objects) {
                ODocument odoc = mapper.toODocument(obj);
                logger.trace("Adaptor done, obj is: " + odoc.toJSON());
                String uuid = odoc.field("uuid");
                logger.trace("Ok, now saving item with uuid = " + uuid);
                ODocument existed = null;

                // Search for exist record                
                String query1 = "SELECT * FROM " + className + " WHERE uuid = '" + uuid + "'";
                List<ODocument> existed_items = db.query(new OSQLSynchQuery<ODocument>(query1));
                logger.trace("Query: " + query1 + ". Result: " + existed_items.size());
                if (!existed_items.isEmpty()) {
                    logger.trace("There is " + existed_items.size() + " existing item with id: " + uuid);
                    for (ODocument eee : existed_items) {
                        logger.trace("  --> Existed item: " + eee.toJSON());
                        existed = eee;
                    }
//                        existed = existed_items.get(0);
                } else {
                    logger.trace("There is NO existing item with id: " + uuid);
                }

                // save new or update it
                if (uuid != null && existed != null) {
                    existed.merge(odoc, true, false);
                    ODocument r = db.save(existed);
                    result.add(r);
                    logger.trace("Merging and saving done odoc object: " + r.toJSON());
                } else {
                    ODocument r = db.save(odoc);
                    result.add(r);
                    logger.trace("Saving done for odoc object: " + r.toJSON());
                }
                String query2 = "SELECT * FROM " + className + " WHERE uuid = '" + uuid + "'";
                List<ODocument> existed_itemsRequery = db.query(new OSQLSynchQuery<ODocument>(query2));
                logger.trace("  --> Save done, " + existed_itemsRequery.size() + " existed items");
                for (ODocument oo : existed_itemsRequery) {
                    System.out.println("  ----> Item is: " + oo.toJSON());
                }
                System.out.println("==================================");

            }
            Long endTime = (new Date()).getTime();
            logger.debug("Save done: " + result.size() + " ODocument(s) in " + (endTime - startTime) + " milisecs -- " + taskID);
            return result;
        } catch (Exception e) {
            logger.error("Error when saving object to DB", e);
            return null;
        } finally {
            manager.closeConnection();
        }
    }

    public T delete(T object) {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        if (db.getMetadata().getSchema().existsClass(className)) {
            try {
                ODocument odoc = mapper.toODocument(object);
                logger.debug("Deleting odoc object: " + odoc.toJSON());
                String uuid = odoc.field("uuid");
                String command = "DELETE FROM " + className + " WHERE uuid = '" + uuid + "'";
                logger.debug("I will execute a query: " + command);
                db.command(new OCommandSQL(command)).execute();
                return object;
            } finally {
                manager.closeConnection();
            }
        } else {
            logger.debug("No class exist: " + className);
        }
        return null;
    }

    // delete all items of the class
    public T deleteAll() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        if (db.getMetadata().getSchema().existsClass(className)) {
            try {
                String command = "DELETE FROM " + className;
                logger.debug("I will execute a query: " + command);
                db.command(new OCommandSQL(command)).execute();
            } finally {
                manager.closeConnection();
            }
        } else {
            logger.debug("No class exist: " + className);
        }

        return null;
    }

    public T read(String uuid) {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        logger.trace("Reading DB: " + uuid);

        if (!db.getMetadata().getSchema().existsClass(className)) {
            logger.debug("No class exist: " + className);
            return null;
        }

        try {
            String query = "SELECT * FROM " + className + " WHERE uuid = '" + uuid + "'";
            List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(query));

//            logger.debug("Query: " + query + ". Result: " + result.size());
            if (!result.isEmpty()) {
                ODocument doc = result.get(result.size() - 1);

//                logger.debug("Read odoc JSON, " + result.size() + " items:" + doc.toJSON());
                logger.trace("End reading: " + uuid);
                return mapper.fromODocument(doc);
            }
        } finally {
            manager.closeConnection();
        }
        return null;
    }

    public List<T> readWithCondition(String whereClause) {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        logger.trace("Read DB with condition: " + whereClause);
        if (!db.getMetadata().getSchema().existsClass(className)) {
            logger.debug("No class exist: " + className);
            return null;
        }
        try {
            String query = "SELECT * FROM " + className + " WHERE " + whereClause;
            List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(query));
            logger.trace("Query: " + query + ". Result: " + result.size());
            List<T> convertedResult = new ArrayList<>();
            if (!result.isEmpty()) {
                for (ODocument doc : result) {
//                    logger.debug("Read with conditions:" + doc.toJSON());
//                    logger.debug("END READ CONDITION====================");
                    convertedResult.add(mapper.fromODocument(doc));
                }
            }
            logger.trace("Read condition done, objects: " + result.size());
            return convertedResult;
        } finally {
            manager.closeConnection();
        }
    }

//    private ODocument readOdocKeepConnection(String uuid) {
//        OrientDBConnector manager = new OrientDBConnector();
//        ODatabaseDocumentTx db = manager.getConnection();
//
//        String query = "SELECT * FROM " + className + " WHERE uuid = '" + uuid + "'";
//        logger.debug("I will execute a query: " + query);
//        List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(query));
//        if (!result.isEmpty()) {
//            return result.get(0);
//        }
//
//        return null;
//    }
    public List<T> readAll() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        logger.trace("Read all: " + className);
        if (!db.getMetadata().getSchema().existsClass(className)) {
            logger.debug("No class exist: " + className);
            return null;
        }
        try {
            String query = "SELECT * FROM " + className;
            List<ODocument> oResult = db.query(new OSQLSynchQuery<ODocument>(query));
//            logger.debug("Query: " + query + ". Result: " + oResult.size());
            List<T> tResult = new ArrayList<>();
            for (ODocument o : oResult) {
                tResult.add(mapper.fromODocument(o));
            }
            logger.trace("Read all done: " + className + ". Objs: " + tResult.size());
            return tResult;
        } finally {
            manager.closeConnection();
        }
    }
}
