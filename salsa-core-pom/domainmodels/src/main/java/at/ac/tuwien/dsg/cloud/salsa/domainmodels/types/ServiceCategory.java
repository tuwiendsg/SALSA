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
package at.ac.tuwien.dsg.cloud.salsa.domainmodels.types;

public enum ServiceCategory {

    VirtualMachine(ServiceStack.Infrastructure),// provide operating system to run other programs    
    Docker(ServiceStack.Infrastructure), // can deploy middleware or app, a container behaviors similar like VM
    
    os(ServiceStack.Infrastructure), // the os stack, similar to VirtualMachine but for higher level view
    software(ServiceStack.Application), // generic type to be compatible with SALSA

    TomcatContainer(ServiceStack.Middleware), // deploy web app, e.g. Tomcat, Apache2, PHP
    DatabaseManagement(ServiceStack.Middleware),// database management system, e.g. MySQL

    SystemService(ServiceStack.Application), // the application that runs continuously as service in the system
    ExecutableApp(ServiceStack.Application), // the application that runs only one time (e.g. via deployment script or start script)
    JavaWebApp(ServiceStack.Application), // the application that is deployed in Tomcat
    ElasticPlatformService(ServiceStack.Application),

    Sensor(ServiceStack.IOT), // not sure about this category, for IOT
    Gateway(ServiceStack.IOT) // not sure about this category, for IOT
    ;

    ServiceStack stack;
    private ServiceCategory(ServiceStack stack) {
        this.stack = stack;
    }

//        VirtualMachine("VirtualMachine"),
//	OS("os"),
//	WAR("war"),
//	DOCKER("docker"), // dd Software container
//	TOMCAT("tomcat"), // dd Software container
//	SOFTWARE("software"), // dd Software artifacts
//        
//        DEVICE("device"),   // for IoT components        
//        SENSOR("sensor"),   // for IoT components  
//        GATEWAY("gateway"),   // for IoT components  
//        
//	EPS("EPS");
}
