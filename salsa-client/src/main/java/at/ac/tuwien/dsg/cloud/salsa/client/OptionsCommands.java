/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client;

import java.io.File;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Duc-Hung LE
 */
public class OptionsCommands {
    @Option(name = "meta", usage = "Get the metadata of the salsa-engine")
    boolean meta;
    
    @Option(name = "service list", usage = "list current service")
    boolean serviceList;
    
    @Option(name = "service submit", metaVar="<toscafile>", usage = "Get the metadata of the salsa-engine")
    File service_submit;
    
    @Option(name = "service status", metaVar="<serviceID>", usage = "Get the metadata of the salsa-engine")            
    String service_status;
    
    
    {
//        System.out.println("Commands:");
//        System.out.println("   meta : Get the metadata of the salsa-engine");        
//        System.out.println("   service list : list current service");
//        System.out.println("   service submit <toscafile> : submit a tosca for deployment");
//        System.out.println("   service status <serviceID> : the configuration status of the service");
//        System.out.println("   service delete <serviceID> : delete the whole service");
//        System.out.println("   units deploy <serviceID>/<unitID> : deploy a new instance of a unit");
//        System.out.println("   instance status <serviceID>/<unitID>/<instanceID> : the configuration status of the instance");
//        System.out.println("   instance delete <serviceID>/<unitID>/<instanceID> : delete the instance");
//        System.out.println("   instance list [category] : list the running instances");
//        System.out.println("   instance category : list the available instance types");
//        System.out.println("   instance info <serviceID>/<unitID>/<instanceID> : query detail information of one instance");
//        System.out.println("   instance query <category> <rule> : query a list of instances");
    }
}
