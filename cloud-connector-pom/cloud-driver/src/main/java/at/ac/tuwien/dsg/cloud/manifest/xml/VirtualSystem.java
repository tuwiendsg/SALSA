package at.ac.tuwien.dsg.cloud.manifest.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualSystem {

	@XmlAttribute
	private String id;
	@XmlAttribute
	private int min;
	@XmlAttribute
	private int max;
	@XmlAttribute
	private int initial;

	@XmlElementWrapper(name = "ProductSection")
	@XmlElement(name = "Property")
	private List<Property> properties;

	public VirtualSystem() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public int getInitial() {
		return initial;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public List<Property> getProperties() {
		return properties;
	}
}
