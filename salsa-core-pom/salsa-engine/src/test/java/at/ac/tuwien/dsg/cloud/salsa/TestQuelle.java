/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa;

import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.QuelleService;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.QUELLE.Recommendations;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author hungld
 */
public class TestQuelle {
    
    public void loadData() {
        
    }
    
    @Test
    public void testLoadQuelleRequirement() throws Exception{
        String fileStr1 = TestQuelle.class.getResource("/quelle_default/requirements.xml").getFile();        
        File file1 = new File(fileStr1);
        JAXBContext jaxbContext1 = JAXBContext.newInstance(MultiLevelRequirements.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        MultiLevelRequirements reqs = (MultiLevelRequirements) jaxbUnmarshaller1.unmarshal(file1);
        Assert.assertEquals("Test if requirement if loaded", "ServiceReqs_overall_elasticity_multi", reqs.getName());        
    }
    
    @Test
    public void testLoadCloudInfo() throws Exception{
        String fileStr2 = TestQuelle.class.getResource("/quelle_default/amazonDescription.xml").getFile();        
        File file = new File(fileStr2);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(CloudProvider.class);
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
        CloudProvider amazone = (CloudProvider) jaxbUnmarshaller2.unmarshal(file);
        Assert.assertEquals("Test if cloud info is loaded", "AmazonEC2", amazone.getName());       
    }
    
    //@Test
    public void testRecommendation() throws Exception{
        String fileStr1 = TestQuelle.class.getResource("/quelle_default/requirements.xml").getFile();        
        File file1 = new File(fileStr1);
        JAXBContext jaxbContext1 = JAXBContext.newInstance(MultiLevelRequirements.class);
        Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
        MultiLevelRequirements reqs = (MultiLevelRequirements) jaxbUnmarshaller1.unmarshal(file1);
        
        String fileStr2 = TestQuelle.class.getResource("/quelle_default/amazonDescription.xml").getFile();        
        File file = new File(fileStr2);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(CloudProvider.class);
        Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
        CloudProvider amazone = (CloudProvider) jaxbUnmarshaller2.unmarshal(file);
        
    }
}
