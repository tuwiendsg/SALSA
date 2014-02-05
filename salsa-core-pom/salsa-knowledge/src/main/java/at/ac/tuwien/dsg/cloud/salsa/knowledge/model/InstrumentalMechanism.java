package at.ac.tuwien.dsg.cloud.salsa.knowledge.model;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.model.impl.InstrumentType;



public abstract class InstrumentalMechanism {
	
	private InstrumentType type;
	
	public abstract void install(String entityName);
	

	public InstrumentType getType() {
		return type;
	}

	public void setType(InstrumentType type) {
		this.type = type;
	}
	
	
}
