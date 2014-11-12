package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab;

public enum StratusLabInstanceType {
        DEFAULT ("m1.small"),
        MEDIUM ("m1.medium"),
        LARGE ("m1.large"),
        XLARGE ("m1.xlarge"),
        MICRO ("t1.micro"),
        MEDIUM_HCPU ("c1.medium"),
        XLARGE_HCPU ("c1.xlarge");

        private final String typeId;

        StratusLabInstanceType(String typeId) {
                this.typeId = typeId;
        }

        public String getTypeId() {
                return typeId;
        }

        public static StratusLabInstanceType getTypeFromString(String val) {
                for (StratusLabInstanceType t : StratusLabInstanceType.values()) {
                        if (t.getTypeId().equals(val)) {
                                return t;
                        }
                }
                return null;
        }
}

