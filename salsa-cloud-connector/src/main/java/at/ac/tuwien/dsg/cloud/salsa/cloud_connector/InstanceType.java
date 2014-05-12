package at.ac.tuwien.dsg.cloud.salsa.cloud_connector;

public enum InstanceType {
	DEFAULT ("m1.small"),
    LARGE ("m1.large"),
    XLARGE ("m1.xlarge"),
    MEDIUM_HCPU ("c1.medium"),
    XLARGE_HCPU ("c1.xlarge"),
    XLARGE_HMEM ("m2.xlarge"),
    XLARGE_DOUBLE_HMEM ("m2.2xlarge"),
    XLARGE_QUAD_HMEM ("m2.4xlarge");

    private final String typeId;

    InstanceType(String typeId) {
            this.typeId = typeId;
    }

    public String getTypeId() {
            return typeId;
    }

    public static InstanceType getTypeFromString(String val) {
            for (InstanceType t : InstanceType.values()) {
                    if (t.getTypeId().equals(val)) {
                            return t;
                    }
            }
            return null;
    }
}
