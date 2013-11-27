package at.ac.tuwien.dsg.cloud.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Object class for monitoring resource allocation
 * 
 * @author alessiogambi
 * 
 */
public class ResourceRequest {
	private static final AtomicInteger idGenerator = new AtomicInteger(0);

	private static final String DEPLOY = "DEPLOY";
	private static final String UNDEPLOY = "UNDEPLOY";

	private final int id;
	private long startTime;
	private long endTime;
	private boolean isError;
	private String imageID;
	private String instanceTypeName;
	private String instanceID;
	private List<String> securityGroups;

	private String type;

	protected ResourceRequest(int id, long startTime, String type) {
		this.id = id;
		this.startTime = startTime;
		this.isError = false;
		this.securityGroups = new ArrayList<String>();
		this.type = type;
	}

	public static synchronized ResourceRequest newDeploy() {
		int theID = idGenerator.incrementAndGet();
		long startTime = System.currentTimeMillis();
		return new ResourceRequest(theID, startTime, DEPLOY);
	}

	public static synchronized ResourceRequest newUndeploy() {
		int theID = idGenerator.incrementAndGet();
		long startTime = System.currentTimeMillis();
		return new ResourceRequest(theID, startTime, UNDEPLOY);
	}

	public final long getEndTime() {
		return endTime;
	}

	public final String getImageID() {
		return imageID;
	}

	public final int getId() {
		return id;
	}

	public final String getInstanceID() {
		return instanceID;
	}

	public final String getInstanceTypeName() {
		return instanceTypeName;
	}

	// This should be immutable ?
	public final List<String> getSecurityGroups() {
		return securityGroups;
	}

	public final long getStartTime() {
		return startTime;
	}

	public void done() {
		this.endTime = System.currentTimeMillis();
	}

	public final boolean isError() {
		return isError;
	}

	public void setError() {
		this.isError = true;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public void setSecurityGroups(List<String> securityGroups) {
		this.securityGroups.clear();
		this.securityGroups.addAll(securityGroups);
	}

	public void setInstanceType(String instanceTypeName) {
		this.instanceTypeName = instanceTypeName;

	}

	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}

	public final String getType() {
		return this.type;
	}
}
