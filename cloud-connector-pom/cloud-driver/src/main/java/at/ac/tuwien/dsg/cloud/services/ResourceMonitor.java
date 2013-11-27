package at.ac.tuwien.dsg.cloud.services;

import at.ac.tuwien.dsg.cloud.data.ResourceRequest;

public interface ResourceMonitor {

	public void record(ResourceRequest request);
}
