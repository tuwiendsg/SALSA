package at.ac.tuwien.dsg.cloud.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.services.InstanceService;

public class InstanceServiceImpl implements InstanceService {

	private Map<String, Object> instanceParameters;

	public InstanceServiceImpl(Logger logger) {
		instanceParameters = new HashMap<String, Object>();
	}

	@Override
	public void put(String key, Object value) {
		instanceParameters.put(key, value);
	}

	@Override
	public Set<String> getKeySet() {
		return instanceParameters.keySet();
	}

	@Override
	public Object get(String key) {
		return instanceParameters.get(key);
	}

}
