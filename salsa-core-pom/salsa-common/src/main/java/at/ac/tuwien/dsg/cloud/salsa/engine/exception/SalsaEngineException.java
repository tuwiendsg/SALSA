package at.ac.tuwien.dsg.cloud.salsa.engine.exception;

import java.io.Serializable;

public class SalsaEngineException extends Exception implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean isServiceError=true;
	
    public SalsaEngineException(boolean isServiceError) {    	
        super();
        this.isServiceError=isServiceError;
    }
    public SalsaEngineException(String msg, boolean isServiceError)   {
        super(msg);
        this.isServiceError=isServiceError;
    }
    public SalsaEngineException(String msg, Exception e, boolean isServiceError)  {
        super(msg, e);
        this.isServiceError=isServiceError;
    }
    
    public boolean isServiceError(){
    	return isServiceError;
    }
}
