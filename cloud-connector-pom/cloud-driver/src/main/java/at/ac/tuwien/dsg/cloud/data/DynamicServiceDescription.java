package at.ac.tuwien.dsg.cloud.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.exceptions.ModifyServiceStateException;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.KeyPairInfo;

/**
 * 
 * 
 * FIXME : Rivedi tutta la logica per aggiungere e togliere macchine e per
 * gestire i loro id. Fai una cosa banale, usa un atomic integer e continua ad
 * aggiugnere. Per togliere le macchine cerca una configurazione che specifica
 * il modo di farlo, altrimenti togli la prima che hai messo. Se fai una FIFO
 * togliere la TAIL. Altrimenti usa una STACK e fai il pop/push usando il
 * contantore per aggiornare i valori.
 * 
 * This class acts as a container for all the properties of the deployed service
 * in a specific moment in time. Since for the moment we manage only one service
 * at time this class is singleton (no more than one instance of this class can
 * exist) -> Not true anymore
 * 
 * @author Alessio Gambi (alessio.gambi@usi.ch)
 * 
 * @author Mario Bisignani (bisignam@usi.ch)
 * 
 *         TODO since the map is already a synchronized one, we still need to
 *         syncrhonize the add and remove replica methods ??
 * 
 *         FIXME apparently this class results in 2 different instances !!!
 * 
 * 
 *         TODO This must be deeploy revisited
 */
public class DynamicServiceDescription {

	private Logger logger = Logger
			.getLogger(at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription.class);

	private StaticServiceDescription staticDes;

	private UUID deployID;

	private KeyPairInfo serviceKey;

	// Maps the a VeeName to the information of running instances
	private Map<String, ArrayList<InstanceDescription>> instances;
	private Map<String, AtomicInteger> replicaNumbers;

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("Object " + this.getClass() + "@" + this.hashCode());
		sb.append("\n");
		sb.append("DeployID " + deployID);
		sb.append("\n");
		for (String instace : instances.keySet()) {
			sb.append("\t");
			sb.append(instace);
			sb.append(":\n");
			for (InstanceDescription description : instances.get(instace)) {
				sb.append("\t\t");
				sb.append(description);
				sb.append("\n");
			}
		}
		String result = sb.toString();
		sb = null;
		return result;
	}

	public DynamicServiceDescription() {
		this(new StaticServiceDescription(null));
	}

	public DynamicServiceDescription(
			DynamicServiceDescription _dynamicServiceDescription) {

		this(_dynamicServiceDescription.staticDes,
				_dynamicServiceDescription.deployID);

		// Force the new objects !
		this.instances = Collections
				.synchronizedMap(new HashMap<String, ArrayList<InstanceDescription>>());

		this.replicaNumbers = Collections
				.synchronizedMap(new HashMap<String, AtomicInteger>());
		// Copy the values
		for (String veeName : _dynamicServiceDescription.instances.keySet()) {
			ArrayList<InstanceDescription> instances = new ArrayList<InstanceDescription>();
			instances.addAll(_dynamicServiceDescription.instances.get(veeName));
			this.instances.put(veeName, instances);
		}
		this.replicaNumbers.putAll(_dynamicServiceDescription.replicaNumbers);

		if (_dynamicServiceDescription.serviceKey != null) {
			this.serviceKey = new KeyPairInfo(
					_dynamicServiceDescription.serviceKey.getKeyName(),
					_dynamicServiceDescription.serviceKey.getKeyFingerprint(),
					_dynamicServiceDescription.serviceKey.getKeyMaterial());
		}

	}

	public DynamicServiceDescription(StaticServiceDescription staticDes) {
		this(staticDes, null);
	}

	public DynamicServiceDescription(StaticServiceDescription staticDes,
			UUID deployID) {

		this.deployID = deployID;
		// initialization
		// Now the map is synchronized
		instances = Collections
				.synchronizedMap(new HashMap<String, ArrayList<InstanceDescription>>());

		replicaNumbers = Collections
				.synchronizedMap(new HashMap<String, AtomicInteger>());

		// instances = new SynchronizedMap<String,
		// ArrayList<InstanceDescription>>();

		// Loop on the vees
		// and create the slots to save their information
		if (staticDes != null) {
			// Assume that getOrderedVee lists all the machines of the service
			for (VeeDescription vee : staticDes.getOrderedVees()) {
				instances.put(vee.getName(),
						new ArrayList<InstanceDescription>());

				replicaNumbers.put(vee.getName(), new AtomicInteger(0));

			}

			logger.info(" Creating from " + staticDes);
		}
		this.staticDes = staticDes;
	}

	public List<InstanceDescription> getVeeInstances(String veeName) {
		if (instances.get(veeName) != null) {
			return instances.get(veeName);
		} else {
			return new ArrayList<InstanceDescription>();
		}
	}

	/**
	 * Return the description of a specific VEE replica
	 * 
	 * @param veeName
	 * @param replicaNum
	 * @return
	 * @throws IllegalArgumentException
	 */
	public InstanceDescription getVeeInstance(String veeName, Integer replicaNum) {

		String errorMessage;

		try {

			InstanceDescription _instance = null;
			for (InstanceDescription instance : getVeeInstances(veeName)) {

				if (replicaNum.equals(Integer.parseInt(FQN
						.getVeeReplicaNumber(instance.getReplicaFQN()
								.toString())))) {
					_instance = instance;
					logger.info("Found Instance " + _instance);
					break;
				}
			}
			return _instance;

		} catch (IndexOutOfBoundsException e) {
			errorMessage = "Replica number "
					+ replicaNum
					+ " of VEE "
					+ veeName
					+ " has not been launched yet, "
					+ "the information you are trying to retrieve are nonexistent";
			logger.error(errorMessage, e);

			throw new IllegalArgumentException(errorMessage, e);
		} catch (Exception e) {
			errorMessage = "Malformed or missing replicaFQN ";
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		}
	}

	// Should be immutable ? Or better a copy?
	public StaticServiceDescription getStaticServiceDescription() {
		return staticDes;
	}

	// THIs must be called after setInstances !
	public void updateReplicaNumbers() {
		for (VeeDescription vee : staticDes.getOrderedVees()) {

			for (InstanceDescription instance : getVeeInstances(vee.getName())) {
				if (instance.getReplicaFQN().getReplicaNum() > replicaNumbers
						.get(vee.getName()).intValue()) {
					logger.info("Update replicaNumber");
					replicaNumbers.get(vee.getName()).set(
							instance.getReplicaFQN().getReplicaNum());
				}
			}

			// TODO Really sure about this ?
			logger.info("Update replicaNumber");
			// As last prepare the NEXT replNum, i.e., ++
			replicaNumbers.get(vee.getName()).incrementAndGet();
		}

	}

	// TODO We provide the Replica number ?!
	public void addVeeInstance(VeeDescription vee, InstanceDescription inst) {
		String errorMessage;

		// Store the machine in the list
		logger.info("Adding " + inst + " to " + vee.getName()
				+ " with replicaFQN " + inst.getReplicaFQN());

		if (!instances.containsKey(vee.getName())) {
			errorMessage = "No VEE with name " + vee.getName()
					+ " has been found in service description, "
					+ "cannot add a new instance for an unknown VEE";
			logger.error(errorMessage);
			return;
		}

		instances.get(vee.getName()).add(inst);

		// Update the replica num counter for the next deployments
		replicaNumbers.get(vee.getName()).incrementAndGet();

	}

	@Deprecated
	public void addVeeInstance(String veeName, Integer replicaNum,
			InstanceDescription inst) throws ModifyServiceStateException {
		String errorMessage;

		if (instances.get(veeName) != null) {

			if (replicaNum > instances.get(veeName).size() - 1) {
				// We need to fill the array until the it reach the required
				// size
				while ((instances.get(veeName).size() - 1) != replicaNum) {
					instances.get(veeName).add(null);
				}
			}

			// Check if the instance we are setting is null
			if (instances.get(veeName).get(replicaNum) == null) {
				// Now we can directly configure the replica at the given
				// index(replicaNum)
				instances.get(veeName).set(replicaNum, inst);
			} else {
				errorMessage = "You are trying to add a VEE instance that already exists, "
						+ "the already existing instance is "
						+ instances.get(veeName).get(replicaNum)
						+ " its replicaNumber is " + replicaNum;
				logger.error(errorMessage);
				throw new ModifyServiceStateException(errorMessage);
			}

		} else {
			errorMessage = "No VEE with name " + veeName
					+ " has been found in service description, "
					+ "cannot add a new instance for an unknown VEE";
			logger.error(errorMessage);
			// throw new IllegalArgumentException(errorMessage);
		}

	}

	public void removeLastReplica(VeeDescription vee)
			throws ModifyServiceStateException {

		String errorMessage;
		String instanceId = null;

		if (!instances.containsKey(vee.getName())) {
			errorMessage = "No VEE with name " + vee.getName()
					+ " has been found in service description, "
					+ "cannot add a new instance for an unknown VEE";
			logger.error(errorMessage);
			return;
		}

		if (instances.get(vee.getName()).size() == 0) {
			errorMessage = "No more VEEs with name " + vee.getName();
			logger.error(errorMessage);
			return;
		}

		// NOTE Should this be configurable ?
		instanceId = instances.get(vee.getName())
				.remove(instances.get(vee.getName()).size() - 1)
				.getInstanceId();

		// try {
		// FQN serviceFQN = new FQN(getStaticServiceDescription()
		// .getServiceFQN().getOrganizationName(),
		// getStaticServiceDescription().getServiceFQN()
		// .getCustomerName(), getStaticServiceDescription()
		// .getServiceFQN().getServiceName());
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Deprecated
	public void removeLastReplica(String veeName)
			throws ModifyServiceStateException {
		String errorMessage;
		String instanceId = null;

		logger.debug("IN REMOVE LAST REPLICA: Size of instances array: "
				+ instances.get(veeName).size());

		if (instances.get(veeName) != null) {

			instanceId = instances.get(veeName)
					.get(instances.get(veeName).size() - 1).getInstanceId();

			if (instances.get(veeName).size() != 0) {

				instances.get(veeName)
						.remove(instances.get(veeName).size() - 1);

				logger.debug(">> IN REMOVE LAST REPLICA after remove: Size of instances array: "
						+ instances.get(veeName).size());

			} else {
				errorMessage = "The list of instances for VEE " + veeName
						+ " is empty, there's no replica to be removed";
				logger.error(errorMessage);
				throw new ModifyServiceStateException(errorMessage);
			}

		} else {
			errorMessage = "No VEE with name "
					+ veeName
					+ " has been found in service description, "
					+ "cannot remove from configuration an instance of an unknown VEE";
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		removeTrailingNullInstances(veeName);
	}

	// Remove a specific replica of a specific VEE from the service description

	public void removeReplica(FQN replicaFQN) {
		String veeName = FQN.getVeeName(replicaFQN.toString());
		if (!instances.containsKey(veeName)) {

			logger.warn("Wrong vee name for remove");

			return;
		}

		// for (InstanceDescription instance : instances.get(veeName)) {
		// if (replicaFQN.equals(instance.getReplicaFQN())) {
		// String instanceId = instance.getInstanceId();
		//
		// FQN serviceFQN = new FQN(getStaticServiceDescription()
		// .getServiceFQN().getOrganizationName(),
		// getStaticServiceDescription().getServiceFQN()
		// .getCustomerName(),
		// getStaticServiceDescription().getServiceFQN()
		// .getServiceName());
		// break;
		// }
		// }

	}

	// Remove a specific replica of a specific VEE from the service description
	@Deprecated
	public void removeReplica(String veeName, Integer replicaNum) {
		String errorMessage;
		String instanceId;

		if (instances.get(veeName) != null) {

			if (instances.get(veeName).get(replicaNum) == null) {

				errorMessage = "Replica number " + replicaNum + " of VEE "
						+ veeName + " has already been removed, "
						+ "unable to remove replica";

				logger.error(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			try {
				instanceId = instances.get(veeName).get(replicaNum)
						.getInstanceId();
			} catch (IndexOutOfBoundsException e) {
				errorMessage = "Replica number " + replicaNum + " of VEE "
						+ veeName + " has already been removed or"
						+ "has never been launched, unable to remove replica";
				logger.error(errorMessage);
				throw new IllegalArgumentException(errorMessage, e);
			}

			// Check if this is the last replica
			if (replicaNum == instances.get(veeName).size() - 1) {

				// In this case we remove it from the list
				instances.get(veeName)
						.remove(instances.get(veeName).size() - 1);

			} else {

				// In this case we set the replica to null because we are in the
				// middle of the replicas array
				instances.get(veeName).set(replicaNum, null);
			}

		} else {
			errorMessage = "No VEE with name "
					+ veeName
					+ " has been found in service description, "
					+ "cannot remove from configuration an instance of an unknown VEE";
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		removeTrailingNullInstances(veeName);

	}

	@Deprecated
	public String getPrivateIp(String veeName, Integer replicaNum)
			throws IllegalArgumentException {
		// This return null getVeeInstance(veeName, replicaNum) or
		// getPrivateIp() ?
		return getVeeInstance(veeName, replicaNum).getPrivateIp()
				.getHostAddress();
	}

	public String getPublicIp(String veeName, Integer replicaNum)
			throws IllegalArgumentException {
		return getVeeInstance(veeName, replicaNum).getPublicIp()
				.getHostAddress();
	}

	public UUID getDeployID() {
		return deployID;
	}

	public void setDeployID(UUID deployID) {
		this.deployID = deployID;
	}

	public VeeDescription getVeeDescription(String veeName) {

		String errorMessage;

		for (VeeDescription vee : staticDes.getOrderedVees()) {
			if (vee.getName().equals(veeName)) {
				return vee;
			}
		}

		errorMessage = "No VEE with name " + veeName
				+ " has been found in service description";
		logger.error(errorMessage);
		throw new IllegalArgumentException(errorMessage);

	}

	public VeeDescription getVeeDescription(FQN veeFQN) {

		String veeName = FQN.getVeeName(veeFQN.toString());
		String errorMessage;

		for (VeeDescription vee : staticDes.getOrderedVees()) {
			if (vee.getName().equals(veeName)) {
				return vee;
			}
		}

		errorMessage = "No VEE with name " + veeName
				+ " has been found in service description";
		logger.error(errorMessage);
		throw new IllegalArgumentException(errorMessage);

	}

	public void setServiceKey(KeyPairInfo serviceKey) {
		this.serviceKey = serviceKey;
	}

	public KeyPairInfo getServiceKey() {
		return serviceKey;
	}

	// public Map<String, ArrayList<InstanceDescription>>
	// getInstances() {
	// return instances;
	// }
	//
	public void setInstances(
			HashMap<String, ArrayList<InstanceDescription>> instances) {
		this.instances = instances;
	}

	/**
	 * Return the last non null replica of the VEE with the name given on input
	 * 
	 * @param veeName
	 * @return
	 */
	private int getLastNonNullReplicaNum(String veeName) {

		for (int i = instances.get(veeName).size() - 1; i >= 0; i--) {
			if (instances.get(veeName).get(i) != null) {
				return i;
			}
		}
		return -1;
	}

	public int getFirstNullReplicaNum(String veeName) {

		if (replicaNumbers.containsKey(veeName)) {
			return replicaNumbers.get(veeName).intValue();
		} else {
			logger.warn("Service " + this + " does not contains " + veeName);
		}
		// for (int i = 0; i < instances.get(veeName).size(); i++) {
		// if (instances.get(veeName).get(i) == null) {
		// return i;
		// }
		// }

		return -1;
	}

	/*
	 * Retrieve the fist non null replica number in the shared structure
	 * excluding the numbers given in input with the excludingNum array
	 */
	@Deprecated
	public int getFirstNullReplicaNum(String veeName,
			ArrayList<Integer> excludingNum) {

		for (int i = 0; i < instances.get(veeName).size(); i++) {
			if (instances.get(veeName).get(i) == null) {
				if (!excludingNum.contains(i)) {
					return i;
				}
			}
		}

		return -1;
	}

	@Deprecated
	private void removeTrailingNullInstances(String veeName) {

		logger.info("In removeTrailingNullInstances for VEE " + veeName);

		int lastNonNull = getLastNonNullReplicaNum(veeName);
		int beforeSize = instances.get(veeName).size() - 1;

		if (lastNonNull != beforeSize) {

			logger.info(beforeSize
					- lastNonNull
					+ " trailing null instances have been found, removing them..");

			// Here we remove the gap between lastNonNull
			// and the size of the instances array
			// for this specific VEE
			for (int i = 0; i < (beforeSize - lastNonNull); i++) {

				logger.info("Removing trailing NULL replica: \n" + "\t\t"
						+ "VEE name: " + veeName + "\n" + "\t\t"
						+ "Replica num: " + i);

				instances.get(veeName)
						.remove(instances.get(veeName).size() - 1);

			}

		} else {
			// no trailing null instances have been found
			logger.info("No trailing null instances have been found");
		}
		logger.info("Remaining instances " + instances.get(veeName));
	}

	@Override
	public boolean equals(Object arg0) {

		if (arg0 instanceof DynamicServiceDescription) {
			DynamicServiceDescription that = (DynamicServiceDescription) arg0;

			if (!("" + this.deployID).equals("" + that.deployID)) {
				return false;
			}

			if (!this.staticDes.equals(that.staticDes)) {
				return false;
			}

			// Check this -> that
			for (String key : this.instances.keySet()) {

				if (!that.instances.containsKey(key)) {
					return false;
				}

				for (InstanceDescription i : this.instances.get(key)) {

					if (!that.instances.get(key).contains(i)) {
						return false;
					}
				}
			}

			// Check that -> this
			for (String key : that.instances.keySet()) {

				if (!this.instances.containsKey(key)) {
					return false;
				}

				for (InstanceDescription i : that.instances.get(key)) {

					if (!this.instances.get(key).contains(i)) {
						return false;
					}
				}
			}
			return true;

		} else {
			return super.equals(arg0);
		}
	}
}
