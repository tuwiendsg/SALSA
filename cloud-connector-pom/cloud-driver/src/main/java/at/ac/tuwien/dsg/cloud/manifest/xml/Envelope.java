package at.ac.tuwien.dsg.cloud.manifest.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class Envelope {

	@XmlElementWrapper(name = "KPISection")
	@XmlElement(name = "KPI")
	private List<KPI> kpiSection;

	@XmlElement(name = "VirtualSystemCollection")
	private VirtualSystemCollection virtualSystemCollection;

	public List<KPI> getKpiSection() {
		return kpiSection;
	}

	public void setKpiSection(List<KPI> kpiSection) {
		this.kpiSection = kpiSection;
	}

	public void setVirtualSystemCollection(
			VirtualSystemCollection virtualSystemCollection) {
		this.virtualSystemCollection = virtualSystemCollection;
	}

	public VirtualSystemCollection getVirtualSystemCollection() {
		return virtualSystemCollection;
	}
}
