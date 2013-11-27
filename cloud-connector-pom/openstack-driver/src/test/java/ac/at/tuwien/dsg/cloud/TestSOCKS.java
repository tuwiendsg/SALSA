package ac.at.tuwien.dsg.cloud;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.usi.controlinterface.client.ControlInterfaceClient;

public class TestSOCKS {

	public static void main(String[] args) {
		try {
			ControlInterfaceClient client = new ControlInterfaceClient(
					InetAddress.getByName("10.99.0.238"), 55555, "Controller");

			client.sendCommandToComponent("shutdown", "ALL");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
