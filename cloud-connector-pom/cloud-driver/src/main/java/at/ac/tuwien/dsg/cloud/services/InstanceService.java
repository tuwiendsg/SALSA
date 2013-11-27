package at.ac.tuwien.dsg.cloud.services;

import java.util.Set;

public interface InstanceService {

	public Set<String> getKeySet();

	public Object get(String key);

	void put(String key, Object value);
}
