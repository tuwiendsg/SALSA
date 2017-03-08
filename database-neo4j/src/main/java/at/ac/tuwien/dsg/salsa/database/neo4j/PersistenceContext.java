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
package at.ac.tuwien.dsg.salsa.database.neo4j;

import at.ac.tuwien.dsg.salsa.database.neo4j.mapper.HashmapFromString;
import at.ac.tuwien.dsg.salsa.database.neo4j.mapper.HashmapToString;
import at.ac.tuwien.dsg.salsa.database.neo4j.mapper.MapFromString;
import at.ac.tuwien.dsg.salsa.database.neo4j.mapper.MapToString;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Duc-Hung Le
 */
@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "at.ac.tuwien.dsg.salsa.database.neo4j")
@ComponentScan("at.ac.tuwien.dsg.salsa")
public class PersistenceContext extends Neo4jConfiguration {

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        System.out.println("DEBUG_SPRING -- get configuration..");

        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config.driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI(new File("graph.db").toURI().toString());
        return config;
    }

    @Bean
    @Override
    public SessionFactory getSessionFactory() {
        System.out.println("DEBUG_SPRING -- get Session Factory..");
        return new SessionFactory(getConfiguration(), "at.ac.tuwien.dsg.salsa");
    }

    @Bean
    protected ConversionService neo4jConversionService() throws Exception {
        ConversionService conversionService = new DefaultConversionService();
        ConverterRegistry registry = (ConverterRegistry) conversionService;
        registry.removeConvertible(Map.class, String.class);
        registry.removeConvertible(String.class, Map.class);
        registry.removeConvertible(HashMap.class, String.class);
        registry.removeConvertible(String.class, HashMap.class);
        //add your own converters like this
        registry.addConverter(new MapFromString());
        registry.addConverter(new MapToString());
        registry.addConverter(new HashmapToString());
        registry.addConverter(new HashmapFromString());

        return conversionService;
    }

}
