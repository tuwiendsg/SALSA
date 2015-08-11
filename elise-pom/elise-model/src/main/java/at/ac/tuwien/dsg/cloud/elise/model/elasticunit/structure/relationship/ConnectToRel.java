package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.relationship;

import javax.xml.bind.annotation.XmlAttribute;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.ServiceUnit;

public class ConnectToRel extends Relationship {

	private static final long serialVersionUID = -8802319806655668518L;

	@XmlAttribute
	protected String capabilityId; // source
	@XmlAttribute
	protected String requirementId; // target
	@XmlAttribute
	protected String variableValue;

	public ConnectToRel() {
	}

	// GENERATED METHODS

	public ConnectToRel(String id, String capabilityId, String requirementId, ServiceUnit from, ServiceUnit to) {
		super();
		this.id = id;
		this.capabilityId = capabilityId;
		this.requirementId = requirementId;
		this.from = from;
		this.to = to;
	}

	public String getCapabilityId() {
		return capabilityId;
	}

	public void setCapabilityId(String capabilityId) {
		this.capabilityId = capabilityId;
	}

	public String getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}

	public String getVariableValue() {
		return variableValue;
	}

	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}

}
