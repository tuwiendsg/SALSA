package at.ac.tuwien.dsg.cloud.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToscaCapabilityString")
@XmlRootElement(name = "ToscaCapabilityString")
public class ToscaCapabilityString {

	@XmlElement(name = "value")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public ToscaCapabilityString(){		
	}
	
	public ToscaCapabilityString(String value){
		this.value = value;
	}
	
}


