package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.openstack.utils.OSSymbolConstants;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import at.ac.tuwien.dsg.cloud.services.UserDataServiceFilter;

public class PlatformUserDataServiceFilter implements UserDataServiceFilter {

	private Logger logger;
	private SymbolSource symbolSource;

	public PlatformUserDataServiceFilter(Logger logger,
			SymbolSource symbolSource) {
		this.logger = logger;
		this.symbolSource = symbolSource;

		// System.out
		// .println("PlatformUserDataServiceFilter.PlatformUserDataServiceFilter() Logger "
		// + logger.getName());
	}

	@Override
	public String getUserData(String inputUserData,
			InstanceService instanceService, UserDataService delegate) {

		// Post Processing
		return injectPlatformValues(delegate.getUserData(inputUserData,
				instanceService));
	}

	private String injectPlatformValue(SymbolSource symbolSource,
			String original, String key, String symbolName) {

		try {
			return original.replaceAll(key,
					symbolSource.valueForSymbol(symbolName));
		} catch (RuntimeException e) {
			logger.warn("Symbol " + symbolName + " not defined");
		}
		return original;
	}

	private String injectPlatformValues(String original) {
		String replacement = original;

		replacement = injectPlatformValue(symbolSource, replacement,
				"@eucaAccessKey", OSSymbolConstants.OS_EC2_ACCESS_KEY);
		replacement = injectPlatformValue(symbolSource, replacement,
				"@osAccessKey", OSSymbolConstants.OS_EC2_ACCESS_KEY);

		replacement = injectPlatformValue(symbolSource, replacement,
				"@accessKey", OSSymbolConstants.OS_EC2_ACCESS_KEY);

		replacement = injectPlatformValue(symbolSource, replacement,
				"@eucaSecretKey", OSSymbolConstants.OS_EC2_SECRET_KEY);

		replacement = injectPlatformValue(symbolSource, replacement,
				"@osSecretKey", OSSymbolConstants.OS_EC2_SECRET_KEY);

		replacement = injectPlatformValue(symbolSource, replacement,
				"@secretKey", OSSymbolConstants.OS_EC2_SECRET_KEY);

		replacement = injectPlatformValue(symbolSource, replacement, "@ccPort",
				OSSymbolConstants.OS_EC2_CC_PORT);
		replacement = injectPlatformValue(symbolSource, replacement,
				"@ccAddress", OSSymbolConstants.OS_EC2_CC_ADDRESS);

		// NOVA STYLE
		replacement = injectPlatformValue(symbolSource, replacement,
				"@novaUsername", OSSymbolConstants.NOVA_USERNAME);
		replacement = injectPlatformValue(symbolSource, replacement,
				"@novaPassword", OSSymbolConstants.NOVA_PASSWORD);
		replacement = injectPlatformValue(symbolSource, replacement,
				"@novaTenantId", OSSymbolConstants.NOVA_TENANT_ID);
		replacement = injectPlatformValue(symbolSource, replacement,
				"@novaTenantName", OSSymbolConstants.NOVA_TENANT_NAME);

		return replacement;
	}

}
