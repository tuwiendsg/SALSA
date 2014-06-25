package at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AssociatedVM")
	public class AssociatedVM {
	    @XmlAttribute(name = "IP")
		private String ip="";
	    @XmlAttribute(name="UUID")
	    private String uuid="";
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

	 
}
