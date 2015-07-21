package at.ac.tuwien.dsg.cloud.salsa.engine.exception;

import java.io.Serializable;

public abstract class SalsaException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    public static class ErrorType{
        public static final int ENGINE_INTERNAL=500;
        public static final int CONFIGURATION_PROCESS=501;
        public static final int CLIENT_BAD_REQUEST=400;        
    }

    public SalsaException() {
        super();
    }

    public SalsaException(String msg) {
        super(msg);        
    }

    public SalsaException(String msg, Exception e) {        
        super(msg, e);
        e.printStackTrace();
    }

    public SalsaException(Exception e) {
        super(e);        
        e.printStackTrace();
    }

    public abstract int getErrorCode();

}
