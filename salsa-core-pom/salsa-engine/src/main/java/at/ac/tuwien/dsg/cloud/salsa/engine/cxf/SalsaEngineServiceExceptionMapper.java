package at.ac.tuwien.dsg.cloud.salsa.engine.cxf;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author omoser
 */
public class SalsaEngineServiceExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(SalsaEngineServiceExceptionMapper.class);


    @Override
    public Response toResponse(Exception exception) {
        return Response.serverError().type(MediaType.APPLICATION_JSON_TYPE).
                entity(new ErrorResponse().withCode(-1).withMessage("Error")).build();
    }

    class ErrorResponse {

        private int code;

        private String message;

        private String documentationUri;

        @JsonProperty("error-code")
        public int getCode() {
            return code;
        }

        @JsonProperty("error-message")
        public String getMessage() {
            return message;
        }

        @JsonProperty("documentation-uri")
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


    }
}
