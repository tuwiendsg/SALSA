package at.ac.tuwien.dsg.cloud.salsa.common.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SalsaRelationshipTypeEnum")
@XmlEnum
public enum SalsaRelationshipType {
	HOSTON("HOSTON"),
	CONNECTTO("CONNECTTO");	
	
	private String relationshipType;
	
	private SalsaRelationshipType(String relationshipType){
		this.relationshipType = relationshipType;
	}

	public String getRelationshipTypeString() {
		return relationshipType;
	}
	
	public static SalsaRelationshipType fromString(String text) {
	    if (text != null) {
	      for (SalsaRelationshipType b : SalsaRelationshipType.values()) {
	        if (text.equalsIgnoreCase(b.getRelationshipTypeString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
