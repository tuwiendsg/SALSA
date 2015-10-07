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
package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SalsaException extends Exception implements Serializable {
    static Logger logger = LoggerFactory.getLogger(SalsaException.class);

    private static final long serialVersionUID = 1L;
    
    public static class ErrorType{
        public static final int ENGINE_INTERNAL=500;
        public static final int CONFIGURATION_PROCESS=501;
        public static final int CLIENT_BAD_REQUEST=400;        
    }

    public SalsaException() {
        super();        
        logger.error("SalsaException: An unknown error occur !");
    }

    public SalsaException(String msg) {
        super(msg);
        logger.error("SalsaException: " + msg);       
        
    }

    public SalsaException(String msg, Exception e) {        
        super(msg, e);
        logger.error("SalsaException: " + msg);        
        e.printStackTrace();
    }

    public SalsaException(Exception e) {
        super(e);
        logger.error("SalsaException: " + e.getMessage());        
        e.printStackTrace();
    }

    public abstract int getErrorCode();

}
