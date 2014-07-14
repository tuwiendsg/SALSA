package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SalsaEntityTypeEnum")
@XmlEnum
public enum SalsaEntityType {
	OPERATING_SYSTEM("os"),
	DOCKER("docker"),
	SOFTWARE("software"),
	SERVICE("service"),
	PROGRAM("program"),
	ARTIFACT("artifact"),
	WAR("war"),
	TOMCAT("tomcat");
	
	private String entityType;
	
	private SalsaEntityType(String entityType){
		this.entityType = entityType;
	}

	public String getEntityTypeString() {
		return entityType;
	}
	
	public static SalsaEntityType fromString(String text) {
	    if (text != null) {
	      for (SalsaEntityType b : SalsaEntityType.values()) {
	        if (text.equalsIgnoreCase(b.getEntityTypeString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
