/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author hungld
 */
@Provider
public class SalsaExceptionResponseMapper implements ExceptionMapper<SalsaException> {

    @Override
    public Response toResponse(SalsaException e) {
        return Response.status(e.getErrorCode()).entity(e.getMessage()).build();
    }
}
