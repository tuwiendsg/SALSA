package at.ac.tuwien.dsg.cloud.salsa.modules;

import java.util.logging.Logger;

import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Scope;

import at.ac.tuwien.dsg.cloud.services.DelayInjectionService;
import at.ac.tuwien.dsg.cloud.services.InstanceService;

public class SalsaCloudAppModule {
	
	public static void contributeSymbolSource(){
		
	}
	
	public DelayInjectionService buildNoDelay(){
		return null;
	}
	
	@Scope(ScopeConstants.PERTHREAD)
	public static InstanceService build(Logger logger){
		return null;
	}
	
	
}
