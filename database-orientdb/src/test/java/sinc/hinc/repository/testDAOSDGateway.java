/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.repository;

import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.AbstractDAO;
import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.CloudServiceDAO;
import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.OrientDBConnector;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 * @author hungld
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testDAOSDGateway {

    @Test
    public void test1_dropDB() {
        OrientDBConnector manager = new OrientDBConnector();
        ODatabaseDocumentTx db = manager.getConnection();
        db.drop();
    }

    @Test
    public void test2_SaveCloudService() {        
        CloudService service = new CloudService();
        service.setName("mySampleService");
        service.setUuid("UUID12345667890");
        ServiceTopology topo = new ServiceTopology();
        topo.setName("Topo1");
        ServiceUnit unit = new ServiceUnit();
        unit.setName("Unit1");
        unit.hasCapability(new Capability("date", "/bin/date"));

        service.hasTopology(topo);
        topo.hasUnit(unit);

        System.out.println(service.toJson());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(service));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
        System.out.println("=========== Object created ===========");
        // write
        CloudServiceDAO csDAO = new CloudServiceDAO();
        ODocument doc = csDAO.save(service);
        System.out.println("Saving cloud service done: " + doc.toJSON());
        
        // read back
        Assert.assertNotNull(doc);
        System.out.println("=========== Object saved ===========");
        
    }

    @Test
    public void test3_ReadSDG() {
        CloudServiceDAO csDAO = new CloudServiceDAO();
        CloudService service = csDAO.read("UUID12345667890");
        System.out.println("Read cloud service done: " + service.toJson());
        Assert.assertEquals("mySampleService", service.getName());        
       
    }
}
