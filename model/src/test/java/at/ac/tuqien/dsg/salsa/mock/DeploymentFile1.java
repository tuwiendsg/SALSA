package at.ac.tuqien.dsg.salsa.mock;

import at.ac.tuwien.dsg.salsa.description.DeploymentFile;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hungld
 */
public class DeploymentFile1 {

    public static void main(String[] args) throws IOException {
        DeploymentFile dp = new DeploymentFile("cloudService1", "my deploy 1");
        dp.hasPlacement("Component01", "pioneer1");
        dp.hasPlacement("Component02", "pioneer1", "pioneer2");
        System.out.println(dp.toJson());
        System.out.println(dp.toYaml());
    }
}
