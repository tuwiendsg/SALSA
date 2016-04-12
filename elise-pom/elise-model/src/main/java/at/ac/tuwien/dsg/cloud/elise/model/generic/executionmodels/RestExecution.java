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

import at.ac.tuwien.dsg.cloud.elise.model.generic.ExtensibleModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Additional parameters for executing capability via REST call
 *
 * @author Duc-Hung LE
 */
public class RestExecution extends ExtensibleModel {

    protected String endpoint;
    protected RestMethod method;
    protected String data;

    public RestExecution() {
        super(RestExecution.class);
    }

    @XmlType
    public static enum RestMethod {

        GET, POST, PUT, DELETE;
    }

    /**
     *
     * @param endpoint URL of the REST service
     * @param method the HTTP method
     * @param data the possible data to send to the REST
     */
    public RestExecution(String endpoint, RestMethod method, String data) {
        super(RestExecution.class);
        this.endpoint = endpoint;
        this.method = method;
        this.data = data;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public RestMethod getMethod() {
        return this.method;
    }

    public String getData() {
        return this.data;
    }

}
