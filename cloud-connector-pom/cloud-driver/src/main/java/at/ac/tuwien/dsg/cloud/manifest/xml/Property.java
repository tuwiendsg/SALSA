package at.ac.tuwien.dsg.cloud.manifest.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

	@XmlAttribute
	private String key;
	@XmlAttribute
	private String value;

	public Property() {
		// TODO Auto-generated constructor stub
	}

	public Property(String propertyName, String propertyValue) {
		this.key = propertyName;
		this.value = propertyValue;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
