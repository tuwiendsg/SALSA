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

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.main.SalsaStackDependenciesGraph;

/**
 *
 * @author Duc-Hung LE
 */
public class DependencyGraphGenerating {

    public static void main(String[] args) throws Exception {

        SalsaStackDependenciesGraph root = new SalsaStackDependenciesGraph("root","root");

        SalsaStackDependenciesGraph jarConf = new SalsaStackDependenciesGraph("sw","jar")
                .hasScript("Script to install Jar")
                .hasOuterDependency(new SalsaStackDependenciesGraph("sw","jre")
                        .hasInnerDependency(new SalsaStackDependenciesGraph("jre:ver","1.7")                                
                                .hasOuterDependency(new SalsaStackDependenciesGraph("os","Ubuntu").hasScript("script to install jre 1.7 on ubuntu"))
                                .hasOuterDependency(new SalsaStackDependenciesGraph("os","CentOS").hasScript("Script to install jre 1.8 on centos")))
                        .hasInnerDependency(new SalsaStackDependenciesGraph("jre:ver","1.8")
                                .hasOuterDependency(new SalsaStackDependenciesGraph("os","Ubuntu").hasScript("script to install jre 1.7 on centos"))
                                .hasOuterDependency(new SalsaStackDependenciesGraph("os","CentOS").hasScript("Script to install jre 1.8 on centos")))
                );

        SalsaStackDependenciesGraph warConf = new SalsaStackDependenciesGraph("sw","war")
                .hasScript("script to install war")
                .hasOuterDependency(new SalsaStackDependenciesGraph("sw","Tomcat")
                        .hasOuterDependency(new SalsaStackDependenciesGraph("os","Ubuntu")                                
                                .hasOuterDependency(new SalsaStackDependenciesGraph("sw","tomcat-ubuntu").hasScript("install tomcat ubuntu")))
                        .hasOuterDependency(new SalsaStackDependenciesGraph("os","Centos")                                
                                .hasOuterDependency(new SalsaStackDependenciesGraph("sw","tomcat-centos").hasScript("install tomcat centos")))
                );
        
        SalsaStackDependenciesGraph ganglia = new SalsaStackDependenciesGraph("sw","ganglia")                
                .hasOuterDependency(new SalsaStackDependenciesGraph("os","Ubuntu")
                        .hasOuterDependency(new SalsaStackDependenciesGraph("sw", "ganglia-ubuntu").hasScript("install ganglia on ubuntu")))
                .hasOuterDependency(new SalsaStackDependenciesGraph("os","CentOS")
                        .hasOuterDependency(new SalsaStackDependenciesGraph("sw", "ganglia-centos").hasScript("install ganglia on centos")));
        
        SalsaStackDependenciesGraph python = new SalsaStackDependenciesGraph("sw","python")                
                .hasOuterDependency(new SalsaStackDependenciesGraph("os","Ubuntu")
                        .hasOuterDependency(new SalsaStackDependenciesGraph("sw", "python-ubuntu").hasScript("install python on ubuntu")))
                .hasOuterDependency(new SalsaStackDependenciesGraph("os","CentOS")
                        .hasOuterDependency(new SalsaStackDependenciesGraph("sw", "python-centos").hasScript("install python on centos")));

        root.hasOuterDependency(jarConf);
        root.hasOuterDependency(warConf);
        root.hasOuterDependency(ganglia);
        root.hasOuterDependency(python);
        System.out.println(root.toXML());

    }
}
