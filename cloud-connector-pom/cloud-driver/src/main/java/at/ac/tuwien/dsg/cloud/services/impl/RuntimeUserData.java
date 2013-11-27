package at.ac.tuwien.dsg.cloud.services.impl;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;

public class RuntimeUserData implements UserDataService {

	private Logger logger;

	public RuntimeUserData(Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getUserData(String inputUserData, InstanceService instance) {

		String userdata = inputUserData;
		for (String key : instance.getKeySet()) {
			try {
				userdata = userdata.replaceAll(key, instance.get(key)
						.toString());
			} catch (Throwable e) {
				logger.warn(String.format(
						"Problems in injecting the value for %s", key), e);
			}
		}

		return userdata;
	}

}
