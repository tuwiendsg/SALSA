package at.ac.tuwien.dsg.cloud.elise.model.type;

public enum DirectiveType {

	// TODO check with Georgiana if the types are right
	CONSTRAINT("SYBLConstraint"),
	STRATEGY("SYBLStrategy"),
	MONITORING("SYBLMonitoring"),
	PRIORITY("SYBLPriority");

	private final String type;

	DirectiveType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public static DirectiveType fromString(String type) {
		if (type != null) {
			for (DirectiveType b : DirectiveType.values()) {
				if (type.equalsIgnoreCase(b.type)) {
					return b;
				}
			}
		}
		return null;
	}
}