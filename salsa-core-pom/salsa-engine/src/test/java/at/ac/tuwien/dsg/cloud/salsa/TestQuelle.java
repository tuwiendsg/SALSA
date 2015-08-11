/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa;

import at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts.CloudProvider;
import at.ac.tuwien.dsg.quelle.cloudServicesModel.requirements.MultiLevelRequirements;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Duc-Hung Le
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
