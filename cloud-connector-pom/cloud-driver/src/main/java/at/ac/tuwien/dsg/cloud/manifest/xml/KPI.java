package at.ac.tuwien.dsg.cloud.manifest.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KPI {
	@XmlAttribute
	private String kpiName;

	public KPI() {
	}

	public String getKPIname() {
		return kpiName;
	}

	public void setKPIname(String kpiName) {
		this.kpiName = kpiName;
	}
}
