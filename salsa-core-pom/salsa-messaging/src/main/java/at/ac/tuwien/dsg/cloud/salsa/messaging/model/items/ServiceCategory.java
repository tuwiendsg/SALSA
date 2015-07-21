package at.ac.tuwien.dsg.cloud.salsa.messaging.model.items;

public enum ServiceCategory {

    VirtualMachine(ServiceStack.Infrastructure),// provide operating system to run other programs
    AppContainer(ServiceStack.Infrastructure), // can deploy middleware or app, a container behaviors similar like VM

    WebContainer(ServiceStack.Middleware), // deploy web app, e.g. Tomcat, Apache2, PHP
    DatabaseManagement(ServiceStack.Middleware),// database management system, e.g. MySQL

    SystemService(ServiceStack.Application), // the application that runs continuously as service in the system
    ExecutableApp(ServiceStack.Application), // the application that runs only one time (e.g. via deployment script or start script)
    WebApp(ServiceStack.Application), // the application that is deployed in a web container
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
