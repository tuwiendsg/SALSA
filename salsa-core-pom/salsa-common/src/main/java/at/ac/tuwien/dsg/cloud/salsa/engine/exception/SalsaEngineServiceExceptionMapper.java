package at.ac.tuwien.dsg.cloud.salsa.engine.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

//import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author omoser, Duc-Hung LE
 */
@Provider
public class SalsaEngineServiceExceptionMapper implements ExceptionMapper<SalsaEngineException> {
    private static final Logger logger = LoggerFactory.getLogger(SalsaEngineServiceExceptionMapper.class);

    @Override
    public Response toResponse(SalsaEngineException exception) {
    	Gson gson = new Gson();    	
    	if (exception.isServiceError()){
    		ErrorResponse er = new ErrorResponse().withCode(500).withMessage("Salsa Service Error: " + exception.getMessage());
    		String json = gson.toJson(er);
       		return Response.serverError().type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    	} else {
    		ErrorResponse er = new ErrorResponse().withCode(404).withMessage("Salsa Client Error: " + exception.getMessage());
    		String json = gson.toJson(er);
       		return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    	}
    }

    class ErrorResponse {
    	private int code;
        private String message;
        private String documentationUri;

        //@JsonProperty("error-code")
        public int getCode() {
            return code;
        }

        //@JsonProperty("error-message")
        public String getMessage() {
            return message;
        }

        //@JsonProperty("documentation-uri")
        public String getDocumentationUri() {
            return documentationUri;
        }

        public ErrorResponse withDocumentationUri(final String documentationUri) {
            this.documentationUri = documentationUri;
            return this;
        }

        public ErrorResponse withCode(final int code) {
            this.code = code;
            return this;
        }

        public ErrorResponse withMessage(final String message) {
            this.message = message;
            return this;
        }
        
//        @Override
//    	public String toString() {
//    	   return "ErrorResponse [error-code=" + code + ", error-message=" + message + ", document-uri="
//    		+ documentationUri + "]";
//    	}
        
    }
    
}
