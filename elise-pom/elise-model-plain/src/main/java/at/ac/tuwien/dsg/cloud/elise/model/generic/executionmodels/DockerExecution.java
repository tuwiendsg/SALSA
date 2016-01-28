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
package at.ac.tuwien.dsg.cloud.elise.model.generic.executionmodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Additional parameters for executing capability via docker container.
 * @author Duc-Hung LE
 */
public class DockerExecution extends PrimitiveToStringConverable {

    protected List<String> exposedPort = new ArrayList<>();

    public DockerExecution() {
    }

    
    public DockerExecution(String... exposes) {
        this.exposedPort.addAll(Arrays.asList(exposes));               
    }

}
