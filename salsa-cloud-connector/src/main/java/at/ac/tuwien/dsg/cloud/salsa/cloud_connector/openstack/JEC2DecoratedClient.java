package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.xerox.amazonws.ec2.BlockDeviceMapping;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.GroupDescription;
import com.xerox.amazonws.ec2.InstanceStateChangeDescription;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;

public class JEC2DecoratedClient extends Jec2 {

	private Logger logger;
	private int maxRetries;

	public JEC2DecoratedClient(String awsAccessId, String awsSecretKey,
			boolean isSecure, Logger logger, int maxRetries) {
		super(awsAccessId, awsSecretKey, isSecure);
		this.logger = logger;
		this.maxRetries = maxRetries;
	}

	public JEC2DecoratedClient(String awsAccessId, String awsSecretKey,
			boolean isSecure, String server, Logger logger, int maxRetries) {
		super(awsAccessId, awsSecretKey, isSecure, server);
		this.logger = logger;
		this.maxRetries = maxRetries;
	}

	public JEC2DecoratedClient(String awsAccessId, String awsSecretKey,
			boolean isSecure, String server, int port, Logger logger,
			int maxRetries) {
		super(awsAccessId, awsSecretKey, isSecure, server, port);
		this.logger = logger;
		this.maxRetries = maxRetries;
	}

	public JEC2DecoratedClient(String awsAccessId, String awsSecretKey,
			Logger logger, int maxRetries) {
		super(awsAccessId, awsSecretKey);
		this.logger = logger;
		this.maxRetries = maxRetries;
	}

	@Override
	public ReservationDescription runInstances(LaunchConfiguration lc)
			throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {
				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(lc);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public ReservationDescription runInstances(String imageId, int minCount,
			int maxCount, List<String> groupSet, String userData, String keyName)
			throws EC2Exception {
		int r = 0;
		while (r < maxRetries) {
			try {
				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(imageId,
						minCount, maxCount, groupSet, userData, keyName);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public ReservationDescription runInstances(String imageId, int minCount,
			int maxCount, List<String> groupSet, String userData,
			String keyName, InstanceType type) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(imageId,
						minCount, maxCount, groupSet, userData, keyName, type);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public ReservationDescription runInstances(String imageId, int minCount,
			int maxCount, List<String> groupSet, String userData,
			String keyName, boolean publicAddr) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {
				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(imageId,
						minCount, maxCount, groupSet, userData, keyName,
						publicAddr);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public ReservationDescription runInstances(String imageId, int minCount,
			int maxCount, List<String> groupSet, String userData,
			String keyName, boolean publicAddr, InstanceType type)
			throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {
				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(imageId,
						minCount, maxCount, groupSet, userData, keyName,
						publicAddr, type);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public ReservationDescription runInstances(String imageId, int minCount,
			int maxCount, List<String> groupSet, String userData,
			String keyName, boolean publicAddr, InstanceType type,
			String availabilityZone, String kernelId, String ramdiskId,
			List<BlockDeviceMapping> blockDeviceMappings) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {
				logger.info("JEC2LoggedClient.runInstances()");
				long start = System.currentTimeMillis();
				ReservationDescription result = super.runInstances(imageId,
						minCount, maxCount, groupSet, userData, keyName,
						publicAddr, type, availabilityZone, kernelId,
						ramdiskId, blockDeviceMappings);
				logger.info("JEC2LoggedClient.runInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.runInstances()");
	}

	@Override
	public List<ReservationDescription> describeInstances(List<String> arg0)
			throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				logger.info("JEC2LoggedClient.describeInstances(List<String> arg0)");

				long start = System.currentTimeMillis();
				List<ReservationDescription> result = super
						.describeInstances(arg0);

				logger.info("JEC2LoggedClient.describeInstances(List<String> arg0) "
						+ (System.currentTimeMillis() - start));

				return result;

			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception("Max Retries reached for describeInstances("
				+ arg0 + ")");
	}

	@Override
	public List<ReservationDescription> describeInstances(String[] instanceIds)
			throws EC2Exception {
		int r = 0;
		while (r < maxRetries) {
			try {

				long start = System.currentTimeMillis();

				logger.info("JEC2LoggedClient.describeInstances(String[] instanceIds)");

				List<ReservationDescription> result = super
						.describeInstances(instanceIds);

				logger.info("JEC2LoggedClient.describeInstances(String[] instanceIds) "
						+ (System.currentTimeMillis() - start));

				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for describeInstances(String[] instanceIds)");
	}

	@Override
	public List<GroupDescription> describeSecurityGroups(List<String> groupNames)
			throws EC2Exception {
		int r = 0;
		while (r < maxRetries) {
			try {
				long start = System.currentTimeMillis();
				logger.info("JEC2LoggedClient.describeSecurityGroupsLIST("
						+ groupNames + ")");
				List<GroupDescription> result = super
						.describeSecurityGroups(groupNames);
				logger.info("JEC2LoggedClient.describeSecurityGroupsLIST() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for .describeSecurityGroupsLIST() ");

	}

	@Override
	public List<GroupDescription> describeSecurityGroups(String[] groupNames)
			throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				long start = System.currentTimeMillis();
				logger.info("JEC2LoggedClient.describeSecurityGroupsARRAY("
						+ Arrays.toString(groupNames) + ")");
				List<GroupDescription> result = super
						.describeSecurityGroups(groupNames);
				logger.info("JEC2LoggedClient.describeSecurityGroupsARRAY() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for .describeSecurityGroupsARRAY() ");
	}

	@Override
	public List<InstanceStateChangeDescription> terminateInstances(
			List<String> arg0) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				long start = System.currentTimeMillis();
				logger.info("JEC2LoggedClient.terminateInstances()");
				List<InstanceStateChangeDescription> result = super
						.terminateInstances(arg0);
				logger.info("JEC2LoggedClient.terminateInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.terminateInstances()");
	}

	@Override
	public List<InstanceStateChangeDescription> terminateInstances(
			String[] instanceIds) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				logger.info("JEC2LoggedClient.terminateInstances()");
				long start = System.currentTimeMillis();
				List<InstanceStateChangeDescription> result = super
						.terminateInstances(instanceIds);
				logger.info("JEC2LoggedClient.terminateInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.terminateInstances() ");
	}

	@Override
	public List<InstanceStateChangeDescription> startInstances(List<String> arg0)
			throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				long start = System.currentTimeMillis();
				logger.info("JEC2LoggedClient.startInstances() ");
				List<InstanceStateChangeDescription> result = super
						.startInstances(arg0);
				logger.info("JEC2LoggedClient.startInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.startInstances() ");
	}

	@Override
	public List<InstanceStateChangeDescription> startInstances(
			String[] instanceIds) throws EC2Exception {

		int r = 0;
		while (r < maxRetries) {
			try {

				long start = System.currentTimeMillis();
				logger.info("JEC2LoggedClient.startInstances()");
				List<InstanceStateChangeDescription> result = super
						.startInstances(instanceIds);
				logger.info("JEC2LoggedClient.startInstances() "
						+ (System.currentTimeMillis() - start));
				return result;
			} catch (EC2Exception e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e.printStackTrace();
						throw new EC2Exception(e1.getMessage());
					}
					r++;
				} else {
					throw e;
				}
			}
		}

		throw new EC2Exception(
				"Max Retries reached for JEC2LoggedClient.startInstances()");
	}

	@Override
	public void deleteSecurityGroup(String name) throws EC2Exception {
		long start = System.currentTimeMillis();
		logger.info("JEC2LoggedClient.deleteSecurityGroup() ");
		super.deleteSecurityGroup(name);
		logger.info("JEC2LoggedClient.deleteSecurityGroup() "
				+ (System.currentTimeMillis() - start));
	}

	@Override
	public void createSecurityGroup(String name, String desc)
			throws EC2Exception {
		try {
			long start = System.currentTimeMillis();
			logger.info("JEC2LoggedClient.createSecurityGroup() ");
			super.createSecurityGroup(name, desc);
			logger.info("JEC2LoggedClient.createSecurityGroup() "

			+ (System.currentTimeMillis() - start));
		} catch (EC2Exception e) {
			if (!e.getMessage().contains("No reason given")) {
				throw e;
			}
		}
	}

}
