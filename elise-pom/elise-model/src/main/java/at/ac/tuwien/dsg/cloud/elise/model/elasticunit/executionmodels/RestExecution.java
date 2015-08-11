/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.executionmodels;

/**
 *
 * @author hungld
 */
public class RestExecution extends PrimitiveToStringConverable {

    String endpoint;
    RestMethod method;
    String data;

    public RestExecution() {
    }

    public static enum RestMethod {

        GET, POST, PUT, DELETE;
    }

    public RestExecution(String endpoint, RestMethod method, String data) {
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
