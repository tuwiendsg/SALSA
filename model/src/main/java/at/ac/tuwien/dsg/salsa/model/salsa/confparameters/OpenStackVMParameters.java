/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.confparameters;

/**
 *
 * @author hungld
 */
public class OpenStackVMParameters {

    // user settings for openstack, get from salsa.engine.properties file
    public static String sshkey = "openstack.sshkey";
    public static String username = "openstack.username";
    public static String password = "openstack.password";
    public static String tenant = "openstack.tenant";
    public static String keystone_endpoint = "openstack.keystone_endpoint";
    public static String nova_api_endpoint = "openstack.nova_api_endpoint";
    public static String region = "openstack.region";

    // generic VM, will be add from Tosca
    public static String provider = "provider";
    public static String vmType = "instanceType";
    public static String vmImage = "baseImage";
    public static String vmPackage = "packages";

    // extra parameters, from Tosca
    public static String vmName = "vm.securityGroup";
    public static String vmMinInst = "vm.mininst";
    public static String vmMaxInst = "vm.maxinst";

    // fill by SALSA to bootstrap Pioneer
    public static String userData = "userData";
}
