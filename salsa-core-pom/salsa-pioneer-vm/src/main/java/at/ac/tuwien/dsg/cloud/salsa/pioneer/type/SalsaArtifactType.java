package at.ac.tuwien.dsg.cloud.salsa.pioneer.type;

/**
 * The artifact type:
 *  - sh: script based deployment. The script should deploy the app and exit after running.
 *  - binary: script or executable program which does not exit after running (e.g jar program)
 *  - apt: using apt-get to install
 *  - chef: only install chef client and let the real deployment for user
 *  - chef-solo: get the input by the software name in Chef Community repository and deploy
 *  - war: copy and remove the war artifact into Tomcat webapps folder.
 * @author hungld
 */
public enum SalsaArtifactType {
	sh("sh"),
        shcont("shcont"),
	apt("apt-get"),
	chef("chef"),
	chefSolo("chef-solo"),        
	war("war"),
        dockerfile("dockerfile"),
        misc("misc");
	
	private String prop;
	
	private SalsaArtifactType(String propString){
		this.prop = propString;
	}

	public String getString() {
		return prop;
	}
	
	public static SalsaArtifactType fromString(String text) {
	    if (text != null) {
	      for (SalsaArtifactType b : SalsaArtifactType.values()) {
	        if (text.equalsIgnoreCase(b.getString())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}