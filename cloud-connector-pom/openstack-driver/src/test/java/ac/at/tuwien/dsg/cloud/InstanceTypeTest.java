package ac.at.tuwien.dsg.cloud;

import com.xerox.amazonws.ec2.InstanceType;

public class InstanceTypeTest {

	public static void main(String[] args) {
		InstanceType instType = InstanceType.LARGE;
		System.out.println("OpenStackTypica.launchInstance()"
				+ instType.getTypeId());
		System.out
				.println("OpenStackTypica.launchInstance()" + instType.name());
		System.out.println("OpenStackTypica.launchInstance()"
				+ instType.ordinal());
		System.out.println("OpenStackTypica.launchInstance()"
				+ instType.toString());
		System.out.println("OpenStackTypica.launchInstance()"
				+ instType.LARGE.getTypeId());

	}
}
