package at.ac.tuwien.dsg.cloud.manifest;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.dsg.cloud.manifest.xml.Property;

// TODO Make all the implementations Tapestry Services !!
public interface ManifestReader {
	// This was added by Alessio to identify the "entry point", a.k.a. IP, of
	// the service

	/**
	 * This return the veeName of the EntryPoint machine in the application
	 */
	public String getEntryPoint();

	public List<String> getVirtualSystemsStartupOrder();

	public int getMinInstancesForVee(String veeName);

	public int getMaxInstancesForVee(String veeName);

	public int getInitInstancesForVee(String veeName);

	public Map<String, Object> getLaunchParameters(String vEEName);

	public Map<String, String> getUserDataFromManifest(String vEEName);

	public List<Property> getProductSectionForVee(String veeName);

}
