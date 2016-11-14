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
public class DockerParameters {

    public static String workingdir = "workingdir";
    public static String newSalsaWorkingDirInsideDocker = "newSalsaWorkingDirInsideDocker";
    public static String preRunByMe = "preRunByMe"; // the command will be added to the docker when preparing the image
}
