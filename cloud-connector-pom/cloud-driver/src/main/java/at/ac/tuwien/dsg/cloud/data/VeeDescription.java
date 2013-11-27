package at.ac.tuwien.dsg.cloud.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class acts as a container for all the VEE static information (i.e., not
 * at INSTANCE level) we read from the manifest that the platform needs to know
 * to deploy an instance of a specific VEE
 * 
 * @author Mario Bisignani (bisignam@usi.ch)
 * 
 */
public class VeeDescription {

	private String baseImageId;

	private HashMap<String, String> properties;

	private String name;

	private int initialInstances;
	private int maxInstances;
	private int minInstances;
	private String instanceType;
	private String sshKeyName;
	private List<String> securityGroups;

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof VeeDescription) {
			VeeDescription that = (VeeDescription) arg0;

			if (!this.baseImageId.equals(that.baseImageId)) {

				return false;
			}

			if (!this.name.equals(that.name)) {
				return false;
			}
			if (this.initialInstances != that.initialInstances) {
				return false;
			}
			if (this.maxInstances != that.maxInstances) {
				return false;
			}
			if (this.minInstances != that.minInstances) {
				return false;
			}
			if (!this.instanceType.equals(that.instanceType)) {
				return false;
			}

			if (sshKeyName != null) {
				if (!this.sshKeyName.equals(that.sshKeyName)) {
					return false;
				}
			}

			// Hope this is fine
			if (!this.properties.equals(that.properties)) {
				return false;
			}

			if (!this.securityGroups.containsAll(that.securityGroups)) {
				return false;
			}

			if (!that.securityGroups.containsAll(this.securityGroups)) {
				return false;
			}
			return true;
		} else {
			return super.equals(arg0);
		}
	}

	public VeeDescription(String name, String baseImageId,
			HashMap<String, String> properties, int initialInstances,
			int maxInstances, int minInstances, String instanceType,
			String sshKeyName, String... securityGroups) {

		// super();
		this.name = name;
		this.baseImageId = baseImageId;
		this.properties = properties;
		this.initialInstances = initialInstances;
		this.maxInstances = maxInstances;
		this.minInstances = minInstances;
		this.instanceType = instanceType;

		this.sshKeyName = sshKeyName;

		this.securityGroups = new ArrayList<String>();
		if (securityGroups != null) {
			try {
				this.securityGroups.addAll(Arrays.asList(securityGroups));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	// Copy constructor
	public VeeDescription(VeeDescription veeDes) {
		this.name = new String(veeDes.name);
		this.baseImageId = new String(veeDes.baseImageId);
		this.properties = new HashMap<String, String>(veeDes.properties);
		this.initialInstances = veeDes.initialInstances;
		this.maxInstances = veeDes.maxInstances;
		this.minInstances = veeDes.minInstances;
		this.instanceType = new String(veeDes.instanceType);
		this.sshKeyName = new String(veeDes.sshKeyName);
		// TODO Is this a deep copy ?
		this.securityGroups = new ArrayList<String>(veeDes.securityGroups);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBaseImageId() {
		return baseImageId;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public int getInitialInstances() {
		return initialInstances;
	}

	public int getMaxInstances() {
		return maxInstances;
	}

	public int getMinInstances() {
		return minInstances;
	}

	public String getSshKeyName() {
		return sshKeyName;
	}

	public void setSshKeyName(String sshKeyName) {
		this.sshKeyName = sshKeyName;
	}

	public List<String> getSecurityGroups() {
		return securityGroups;
	}

	// public void setSecurityGroup(String securityGroup) {
	// this.securityGroup = securityGroup;
	// }

	public void setBaseImageId(String baseImageId) {
		this.baseImageId = baseImageId;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public void setInitialInstances(int initialInstances) {
		this.initialInstances = initialInstances;
	}

	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}

	public void setMinInstances(int minInstances) {
		this.minInstances = minInstances;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	@Override
	public String toString() {
		return "VeeDescription [baseImageId=" + baseImageId + ", properties="
				+ properties + ", name=" + name + ", initialInstances="
				+ initialInstances + ", maxInstances=" + maxInstances
				+ ", minInstances=" + minInstances + ", instanceType="
				+ instanceType + ", sshKeyName=" + sshKeyName
				+ ", securityGroups = " + securityGroups + "]";
	}

}
