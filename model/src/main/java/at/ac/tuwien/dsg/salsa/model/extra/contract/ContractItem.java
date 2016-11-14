package at.ac.tuwien.dsg.salsa.model.extra.contract;

public class ContractItem {

    Long id;
    private String name;

    private String service;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
