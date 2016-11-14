package at.ac.tuwien.dsg.salsa.model.extra.contract;

public class Script {

    private Long id;

    private String name;

    private String code;

    private String version;

    public Script() {
    }

    public Script(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
