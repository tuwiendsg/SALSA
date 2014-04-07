package at.ac.tuwien.dsg.cloud.salsa.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToscaCapabilityString")
@XmlRootElement(name = "ToscaCapabilityString")
public class SalsaCapaReqString {

	@XmlAttribute(name = "id")
	private String id;
	@XmlAttribute(name = "value")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public SalsaCapaReqString(){		
	}
	
	public SalsaCapaReqString(String id, String value){
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SalsaCapabilityString [id=" + id + ", value=" + value + "]";
	}
	
}


