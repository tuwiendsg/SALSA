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
public class VirtualSystemCollection {

	@XmlElementWrapper(name = "StartupSection")
	@XmlElement(name = "Item")
	private List<StartupItem> startupSection;

	@XmlElement(name = "VirtualSystem")
	private List<VirtualSystem> virtualSystems;

	@XmlAttribute
	private String id;
	@XmlAttribute
	private String entrypoint;

	public VirtualSystemCollection() {
		// TODO Auto-generated constructor stub
	}

	public String getEntrypoint() {
		return entrypoint;
	}

	public String getId() {
		return id;
	}

	public void setEntrypoint(String entrypoint) {
		this.entrypoint = entrypoint;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<StartupItem> getStartupSection() {
		return startupSection;
	}

	public List<VirtualSystem> getVirtualSystems() {
		return virtualSystems;
	}

	public void setStartupSection(List<StartupItem> startupSection) {
		this.startupSection = startupSection;
	}

	public void setVirtualSystems(List<VirtualSystem> virtualSystems) {
		this.virtualSystems = virtualSystems;
	}
}
