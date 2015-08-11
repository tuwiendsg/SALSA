package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.relationship;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.ServiceUnit;

public class LocalRel extends Relationship {

	private static final long serialVersionUID = 7196851315307314559L;

	public LocalRel() {
	}

	public LocalRel(String id, ServiceUnit from, ServiceUnit to) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
	}

}
