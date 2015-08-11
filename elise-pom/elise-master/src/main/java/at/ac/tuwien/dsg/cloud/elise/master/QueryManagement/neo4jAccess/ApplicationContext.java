/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
