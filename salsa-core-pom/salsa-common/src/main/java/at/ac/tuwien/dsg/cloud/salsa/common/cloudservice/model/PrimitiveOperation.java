package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Primitive")
public class PrimitiveOperation {
	@XmlAttribute(name = "name")
	String name;
	// for performing the operation
	@XmlAttribute(name = "type")
	ExecutionType executionType = ExecutionType.SCRIPT;
	@XmlElement(name = "executionREF")
	String executionREF = "/bin/date";
	@XmlElement(name = "executionParameter")
	String executionParameter = "";
	@XmlElement(name = "executionOutput")
	String executionOutput = "";
		
		
	public enum ExecutionType{
		SCRIPT, RESTful, SALSA_CONNECTOR;
	}
	
	public PrimitiveOperation() {}
	
	public static PrimitiveOperation newCommandType(String name, String executionCommand){		
		PrimitiveOperation po = new PrimitiveOperation();
		po.executionREF = executionCommand;
		po.executionType = ExecutionType.SCRIPT;
		po.name = name;
		return po;
	}
	
	public static PrimitiveOperation newScriptFromURLType(String name, String scriptRelativePath){
		PrimitiveOperation po = new PrimitiveOperation();		
		po.executionType = ExecutionType.SCRIPT;
		po.executionREF = scriptRelativePath;		
		po.name = name;
		return po;
	}
	
	public static PrimitiveOperation newSalsaConnector(String name, String connectorName){
		PrimitiveOperation po = new PrimitiveOperation();		
		po.executionType = ExecutionType.SALSA_CONNECTOR;
		po.executionREF = connectorName;
		po.name = name;
		return po;
	}

	public ExecutionType getExecutionType() {
		return executionType;
	}

	public void setExecutionType_(ExecutionType executionType) {
		this.executionType = executionType;
	}

	public String getExecutionREF() {
		return executionREF;
	}

	public void setExecutionREF_(String executionREF) {
		this.executionREF = executionREF;
	}

	public String getExecutionParameter() {
		return executionParameter;
	}

	public void setExecutionParameter_(String executionParameter) {
		this.executionParameter = executionParameter;
	}
	
	
}
