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
package at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Duc-Hung Le
 */
@Configuration
@EnableNeo4jRepositories
@ComponentScan
@Transactional
public class ApplicationContext extends Neo4jConfiguration {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    
    public ApplicationContext() {
        setBasePackage("at.ac.tuwien.dsg.cloud.elise");
    }
//    @Autowired
//    protected GraphDatabaseService db;

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() {
        System.out.println("Creating the GraphDatabaseService bean ... !");
        return new GraphDatabaseFactory().newEmbeddedDatabase(EliseConfiguration.DATA_BASE_STORAGE);
        //return new SpringRestGraphDatabase(EliseConfiguration.DATA_BASE_SEPARATE_ENDPOINT);
    }
    
    @Bean
    public Neo4jTemplate neo4jBean() {
          return new Neo4jTemplate(graphDatabaseService());
    }
}
