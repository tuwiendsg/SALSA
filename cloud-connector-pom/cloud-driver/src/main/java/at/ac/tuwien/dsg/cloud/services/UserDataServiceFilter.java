package at.ac.tuwien.dsg.cloud.services;

public interface UserDataServiceFilter {

	public String getUserData(String inputUserData,
			InstanceService instanceService, UserDataService delegate);
}
