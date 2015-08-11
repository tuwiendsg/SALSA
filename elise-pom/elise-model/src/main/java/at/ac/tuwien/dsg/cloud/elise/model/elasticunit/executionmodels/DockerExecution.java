/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.executionmodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scala.Int;

/**
 *
 * @author hungld
 */
public class DockerExecution extends PrimitiveToStringConverable {

    List<String> exposedPort = new ArrayList<>();

    public DockerExecution() {
    }

    
    public DockerExecution(String... exposes) {
        this.exposedPort.addAll(Arrays.asList(exposes));               
    }

}
