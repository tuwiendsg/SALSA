package at.ac.tuwien.dsg.cloud.elise.model.type;

public enum RelationshipType {

	CONNECT_TO("CONNECTTO"),
	HOST_ON("HOSTON"),
	LOCAL("LOCAL");

	private String type;

	RelationshipType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public static RelationshipType fromString(String type) {
		if (type != null) {
			for (RelationshipType b : RelationshipType.values()) {
				if (type.equalsIgnoreCase(b.type)) {
					return b;
				}
			}
		}
		return null;
	}
}