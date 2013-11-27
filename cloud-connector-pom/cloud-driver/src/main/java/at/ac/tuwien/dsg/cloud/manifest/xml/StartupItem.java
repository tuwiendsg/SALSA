package at.ac.tuwien.dsg.cloud.manifest.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StartupItem {

	@XmlAttribute
	private String id;
	@XmlAttribute
	private int order;

	public StartupItem() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getId() {
		return id;
	}

	public int getOrder() {
		return order;
	}
}
