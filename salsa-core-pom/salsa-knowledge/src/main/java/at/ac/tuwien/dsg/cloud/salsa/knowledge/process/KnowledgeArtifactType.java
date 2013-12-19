package at.ac.tuwien.dsg.cloud.salsa.knowledge.process;

public enum KnowledgeArtifactType {
	salsaClass("salsa-class","java"), 
	knowledgePackage("knowledge-package","jar"),
	artifact("artifact");

	private String name;
	private String type;
	
	private KnowledgeArtifactType(String name) {
		this.name = name;
	}
	
	private KnowledgeArtifactType(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getNameString() {
		return name;
	}
	
	public String getTypeString(){
		return type;
	}

	public static KnowledgeArtifactType fromNameString(String text) {
		if (text != null) {
			for (KnowledgeArtifactType b : KnowledgeArtifactType.values()) {
				if (text.equalsIgnoreCase(b.getNameString())) {
					return b;
				}
			}
		}
		return null;
	}

}
