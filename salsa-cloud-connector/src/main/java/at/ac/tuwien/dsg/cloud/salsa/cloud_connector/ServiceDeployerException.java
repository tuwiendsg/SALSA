package at.ac.tuwien.dsg.cloud.salsa.cloud_connector;

public class ServiceDeployerException extends Exception {
	private static final long serialVersionUID = 5244389801522133902L;

	public ServiceDeployerException() {
	}

	public ServiceDeployerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceDeployerException(String arg0) {
		super(arg0);
	}

	public ServiceDeployerException(Throwable arg0) {
		super(arg0);
	}
}
