package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds;

public enum ConnectorsEnum {

	DSG_OPENSTACK("dsg@openstack"), LAL_STRATUSLAB("lal@stratuslab");

	private String providerId;

	private ConnectorsEnum(String providerId) {
		this.providerId = providerId;
	}

	public String getCloudProviderString() {
		return providerId;
	}

	public static ConnectorsEnum fromString(String text) {
		if (text != null) {
			for (ConnectorsEnum b : ConnectorsEnum.values()) {
				if (text.equalsIgnoreCase(b.getCloudProviderString())) {
					return b;
				}
			}
		}
		return null;
	}

}
