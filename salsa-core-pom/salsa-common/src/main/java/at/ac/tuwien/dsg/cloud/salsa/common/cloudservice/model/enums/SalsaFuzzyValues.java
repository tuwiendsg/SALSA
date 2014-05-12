package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SalsaFuzzyValues")
@XmlEnum
public enum SalsaFuzzyValues {
	high("high"),
	medium("medium"),
	low("low"),
	udefined("udefined");	
	
	private String value;
	
	private SalsaFuzzyValues(String value){
		this.value = value;
	}

	public String getValueString() {
		return value;
	}
		
	public static SalsaFuzzyValues fromString(String text) {
	    if (text != null) {
	      for (SalsaFuzzyValues b : SalsaFuzzyValues.values()) {
	        if (text.equalsIgnoreCase(b.getValueString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
