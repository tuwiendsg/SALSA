package at.ac.tuwien.dsg.cloud.manifest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.exceptions.UserDataException;
import at.ac.tuwien.dsg.cloud.manifest.utils.DeploymentPropertiesManagementUtils;
import at.ac.tuwien.dsg.cloud.manifest.xml.Envelope;
import at.ac.tuwien.dsg.cloud.manifest.xml.KPI;
import at.ac.tuwien.dsg.cloud.manifest.xml.Property;
import at.ac.tuwien.dsg.cloud.manifest.xml.StartupItem;
import at.ac.tuwien.dsg.cloud.manifest.xml.VirtualSystem;

/**
 * This class should replace or extend the actual OVF Parser. Maybe we can find
 * a simple way to deal with... FOR THE MOMENT IS JUST COPY/PAST/EXTENDS
 * 
 * NOTE THIS IS ONLY FOR BACKWARDS COMPATIBILITY !!!
 * 
 * @author alessiogambi
 * 
 */
public class XMLManifestReader implements ManifestReader {

	private Logger logger = Logger.getLogger(XMLManifestReader.class);

	private Envelope serviceManifest;

	private void loadFromFile(InputStream inputStream) throws JAXBException,
			IOException {
		JAXBContext contextObj = JAXBContext.newInstance(Envelope.class);
		Unmarshaller unMarshallerObj = contextObj.createUnmarshaller();
		serviceManifest = (Envelope) unMarshallerObj
				.unmarshal(new BufferedReader(
						new InputStreamReader(inputStream)));

	}

	public XMLManifestReader(InputStream ovfInputStream) {

		logger.debug("Parsing the manifest");
		try {
			loadFromFile(ovfInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getEntryPoint() {
		return serviceManifest.getVirtualSystemCollection().getEntrypoint();
	}

	public List<String> getKpiNames() {
		List<String> kpiNames = new ArrayList<String>();
		for (KPI kpi : serviceManifest.getKpiSection()) {
			kpiNames.add(kpi.getKPIname());
		}
		return kpiNames;
	}

	public List<String> getVirtualSystemsStartupOrder() {
		List<String> result = new ArrayList<String>();
		for (StartupItem item : serviceManifest.getVirtualSystemCollection()
				.getStartupSection()) {
			result.add(item.getId());
		}
		return result;

	}

	private VirtualSystem getVirtualSystemByName(String veeName) {
		for (VirtualSystem virtualSystem : serviceManifest
				.getVirtualSystemCollection().getVirtualSystems()) {
			if (virtualSystem.getId().equalsIgnoreCase(veeName))
				return virtualSystem;
		}
		// Throw an exception ? Anyway this will result in an exception
		return null;
	}

	public int getMinInstancesForVee(String veeName) {
		return getVirtualSystemByName(veeName).getMin();
	}

	public int getMaxInstancesForVee(String veeName) {
		return getVirtualSystemByName(veeName).getMax();
	}

	public int getInitInstancesForVee(String veeName) {
		return getVirtualSystemByName(veeName).getInitial();
	}

	public Map<String, String> getUserDataFromManifest(String veeName) {

		String errorMessage;

		HashMap<String, String> userDataProps = new HashMap<String, String>();

		for (Property prop : getVirtualSystemByName(veeName).getProperties()) {

			if (prop.getKey()
					.startsWith(
							DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix)) {

				// Get the userdata key
				String userDataPropKey = prop
						.getKey()
						.substring(
								prop.getKey()
										.indexOf(
												DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix)
										+ DeploymentPropertiesManagementUtils.euca_ovf_userdata_prefix
												.length());

				logger.debug("Userdata definition has been found for VEE "
						+ veeName + "\n" + "\t Key: \t" + userDataPropKey
						+ "\n" + "\t Value: \t" + prop.getValue());

				// Check that the userdata key is not in the list of reserved
				// keys
				if (DeploymentPropertiesManagementUtils.userDataReservedKeys
						.contains(userDataPropKey)) {

					errorMessage = "The userdata key "
							+ userDataPropKey
							+ " is in the list of reserved user data keys"
							+ " you must avoid to use keys "
							+ DeploymentPropertiesManagementUtils.userDataReservedKeys
							+ " when you pass user-data"
							+ " from the manifest to the eucalyptus platform";

					logger.error(errorMessage);
					throw new RuntimeException(new UserDataException(
							errorMessage));

				}

				userDataProps.put(prop.getKey(), prop.getValue());
			} else if (prop.getKey().compareTo("STARTUP") == 0) {
				// Get the userdata key
				String userDataPropKey = prop.getKey();

				logger.debug("Startup definition has been found for VEE "
						+ veeName + "\n" + "\t Key: \t" + userDataPropKey
						+ "\n" + "\t Value: \t" + prop.getValue());

				userDataProps.put(prop.getKey(), prop.getValue());
			} else {
				logger.debug("Manifest Property : " + prop.getKey()
						+ " is not a valid USERDATA or STARTUP !");
			}
		}

		return userDataProps;
	}

	public Map<String, Object> getLaunchParameters(String veeName) {

		String errorMessage;

		HashMap<String, Object> launchParameters = new HashMap<String, Object>();

		for (Property prop : getVirtualSystemByName(veeName).getProperties()) {

			if (DeploymentPropertiesManagementUtils.requiredlaunchParametersKeys
					.contains(prop.getKey())) {
				launchParameters.put(prop.getKey(), prop.getValue());
			} else if (DeploymentPropertiesManagementUtils.launchParametersKeys
					.contains(prop.getKey())) {
				launchParameters.put(prop.getKey(), prop.getValue());
			}
		}

		// Parse String SECURITY_GROUP into String[]
		if (launchParameters.containsKey("SECURITY_GROUP")) {
			String _secGroups = (String) launchParameters.get("SECURITY_GROUP");
			launchParameters.put("SECURITY_GROUP", _secGroups.split(","));
		}

		// Check that the required LaunchParameter has been defined
		if (!launchParameters
				.keySet()
				.containsAll(
						DeploymentPropertiesManagementUtils.requiredlaunchParametersKeys)) {
			errorMessage = "Missing launch parameter. "
					+ DeploymentPropertiesManagementUtils.requiredlaunchParametersKeys;

			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);

		} else {
			logger.info("XMLManifestReader.getLaunchParameters() "
					+ launchParameters);
			return launchParameters;
		}

	}

	public ArrayList<Property> getProductSectionForVee(String veeName) {
		// TODO Auto-generated method stub
		return null;
	}

	// private ServiceLevelObjective createServiceLevelObjective(
	// PerformanceObjectiveSectionType value) {
	// ConstraintType constraint = value.getConstraint();
	//
	// ServiceLevelObjective slo = new ServiceLevelObjective();
	// slo.KPIName = constraint.getKPIName();
	// RangeType range = constraint.getRange();
	// slo.maxThreshold = range.getMax().intValue();
	// slo.minThreshold = range.getMin().intValue();
	// slo.objectiveName = constraint.getName();
	// slo.percentage = (constraint.getPercentage().intValue() /
	// 100.0D);
	//
	// long window = constraint.getWindow().getValue().longValue();
	// switch (constraint.getWindow().getUnit().ordinal()) {
	// case 1:
	//
	// window *= 3600000L;
	// break;
	// case 2:
	//
	// window *= 60000L;
	// break;
	// case 3:
	//
	// window *= 1000L;
	// }
	//
	// slo.window = window;
	// return slo;
	// }

	// private void initReaderFields() {
	// logger.debug("Storing the VirtualSystems information");
	// initVirtualSystems();
	//
	// logger
	// .debug("Storing the VirtualSystems StartupOrder information");
	// initVirtualSystemStartupOrder();
	//
	// initEntryPoint();
	// }
	//
	// private void initEntryPoint() {
	// if (this.virtualSystemCollection == null) {
	// return;
	// }
	//
	// this.entryPoint = virtualSystemCollection.getOtherAttributes().get(
	// new QName("entrypoint"));
	// }
	//
	// private void initVirtualSystems() {
	// if (this.virtualSystemCollection == null) {
	// return;
	// }
	//
	// for (Object content : this.virtualSystemCollection
	// .getContent()) {
	// if (content instanceof JAXBElement) {
	// JAXBElement element = (JAXBElement) content;
	//
	// if (element.getValue() instanceof VirtualSystemType) {
	// this.virtualSystems
	// .add((VirtualSystemType) element.getValue());
	//
	// logger
	// .debug("New VirtualSystem instance has been found in the VirtualSystemCollection \n\tValue: \t"
	// + element.getValue()
	// + "\n"
	// + "\t"
	// + "Type: \t"
	// + element.getValue().getClass()
	// .getCanonicalName());
	// }
	// }
	// }
	// }
	//
	// private void initVirtualSystemStartupOrder() {
	// if (this.virtualSystemCollection == null) {
	// return;
	// }
	// try {
	// StartupSectionType startupSection = (StartupSectionType)
	// OVFEnvelopeUtils
	// .getSection(this.virtualSystemCollection,
	// StartupSectionType.class);
	//
	// logger
	// .debug("StartupSection has been found in the VirtualSystemCollection \n\tValue: \t"
	// + startupSection.toString());
	//
	// this.virtualSystemsStartupOrder =
	// readVirtualSystemsStartupOrder(startupSection);
	// } catch (SectionNotPresentException e) {
	// String errorMessage =
	// "no StartupSection has been found in the VirtualSystemCollection";
	// logger.error(errorMessage);
	// // throw new SectionNotPresentException(errorMessage, e);
	// } catch (InvalidSectionException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private ArrayList<String> readVirtualSystemsStartupOrder(
	// StartupSectionType startupSection) {
	//
	// String errorMessage = new String();
	//
	// ArrayList<Integer> tmpOrderValuesList = new ArrayList<Integer>();
	// HashMap<String, Integer> tmpStartupOrderMap = new HashMap<String,
	// Integer>();
	//
	// ArrayList<String> ouputStartupOrderedVeeNamesList = new
	// ArrayList<String>();
	//
	// boolean veeStartupOrderItemFound;
	//
	// for (String veeName : getVeeTypeNames()) {
	// veeStartupOrderItemFound = false;
	//
	// for (StartupItem.Item startupItem : startupSection.getItem()) {
	// if (startupItem.getId().equals(veeName)) {
	// veeStartupOrderItemFound = true;
	// tmpStartupOrderMap.put(veeName,
	// Integer.valueOf(startupItem.getOrder()));
	// if (!tmpOrderValuesList.contains(Integer
	// .valueOf(startupItem.getOrder()))) {
	// tmpOrderValuesList.add(Integer.valueOf(startupItem
	// .getOrder()));
	// }
	// }
	// }
	//
	// // Default put it as last
	// if (!(veeStartupOrderItemFound)) {
	// errorMessage = "No StartupOrder definition has been found for VEE "
	// + veeName + " Start at LAST ";
	// logger.warn(errorMessage);
	// tmpStartupOrderMap.put(veeName, Integer.MAX_VALUE);
	// tmpOrderValuesList.add(Integer.MAX_VALUE);
	// }
	//
	// }
	//
	// // Sort the values
	// Collections.sort(tmpOrderValuesList);
	//
	// // Build the List. Vee with same ID are put one after the other
	// for (Object orderValue : tmpOrderValuesList) {
	// for (Entry<String, Integer> vee : tmpStartupOrderMap.entrySet()) {
	// if (vee.getValue().equals(orderValue)) {
	// ouputStartupOrderedVeeNamesList.add(vee.getKey());
	// }
	// }
	// }
	//
	// return ouputStartupOrderedVeeNamesList;
	// }
	//
	//
	// private int getAttributeForVee(String veeName, String attr) {
	// for (VirtualSystemType vs : virtualSystems) {
	// if (vs.getId().equals(veeName)) {
	// return getIntValueForAttribute(vs, attr);
	// }
	// }
	// throw new RuntimeException(
	// "No VirtualSystem with name attribute equals to " + veeName
	// + " has been found in ovf readed from inputstream "
	// + this.inputStream.toString());
	// }
	//
	// public ArrayList<String> getSubNodeNamesForVee(String veeName) {
	// for (VirtualSystemType vs : this.virtualSystems) {
	// if (vs.getId().equals(veeName)) {
	// return getVirtualSystemSubNodesNames(vs);
	// }
	// }
	// throw new RuntimeException(
	// "No VirtualSystem with name attribute equals to " + veeName
	// + " has been found in ovf readed from inputstream "
	// + this.inputStream.toString());
	// }
	//
	// private String getAttributeForVee(String veeName, String namespace,
	// String attr) {
	// for (VirtualSystemType vs : this.virtualSystems) {
	// if (vs.getId().equals(veeName)) {
	// return getIntValueForAttribute(vs, attr, namespace);
	// }
	// }
	// throw new RuntimeException(
	// "No VirtualSystem with name attribute equals to " + veeName
	// + " has been found in ovf readed from inputstream "
	// + this.inputStream.toString());
	// }
	//
	// public ArrayList<ProductSectionType.Property> getProductSectionForVee(
	// String className, String veeName) {
	// for (VirtualSystemType vs : this.virtualSystems) {
	// if (vs.getId().equals(veeName)) {
	// return getProductSection(vs, className);
	// }
	// }
	// throw new RuntimeException(
	// "No VirtualSystem with name attribute equals to " + veeName
	// + " has been found in ovf readed from inputstream "
	// + this.inputStream.toString());
	// }
	//
	// public List<String> getVeeTypeNames() {
	// List veeTypes = new ArrayList();
	//
	// for (VirtualSystemType vs : this.virtualSystems) {
	// veeTypes.add(vs.getId());
	// }
	//
	// return veeTypes;
	// }
	//
	// private int getIntValueForAttribute(VirtualSystemType vs, String string)
	// {
	// QName a = null;
	// String value = null;
	//
	// // OVF/TELEFONICA Namespace
	// a = new QName("http://schemas.telefonica.com/claudia/ovf", string);
	// value = (String) vs.getOtherAttributes().get(a);
	//
	// if (value == null) {
	// // Look also in default Namespace
	// a = new QName("", string);
	// value = (String) vs.getOtherAttributes().get(a);
	// }
	//
	// if (value == null) {
	// String erroMessage = "Attribute " + string
	// + " not present.";
	// logger.error(erroMessage);
	// throw new RuntimeException(erroMessage);
	// }
	//
	// return Integer.valueOf(value).intValue();
	// }
	//
	// private String getIntValueForAttribute(VirtualSystemType vs,
	// String attribute, String namespace) {
	// QName a = new QName(namespace, attribute);
	//
	// String value = (String) vs.getOtherAttributes().get(a);
	//
	// if (value == null) {
	// String errorMessage = "Attribute " + attribute
	// + " not present for virtualSystem " + vs.getId()
	// + " and namespace " + namespace;
	// logger.error(errorMessage);
	// throw new RuntimeException(errorMessage);
	// }
	//
	// return value;
	// }
	//
	// private ArrayList<String> getVirtualSystemSubNodesNames(VirtualSystemType
	// vs) {
	// ArrayList nodesNames = new ArrayList();
	//
	// for (JAXBElement nodeElem : vs.getSection()) {
	// nodesNames.add(nodeElem.getName().toString());
	// }
	//
	// return nodesNames;
	// }
	//
	// private ArrayList<ProductSectionType.Property> getProductSection(
	// VirtualSystemType vs, String className) {
	// ArrayList productProperties = new ArrayList();
	//
	// for (JAXBElement nodeElem : vs.getSection()) {
	// if (nodeElem.getValue() instanceof ProductSectionType) {
	// ProductSectionType prod = (ProductSectionType) nodeElem
	// .getValue();
	//
	// if (prod.getClazz().equals(className)) {
	// for (Iterator i$ = prod.getCategoryOrProperty()
	// .iterator(); i$.hasNext();) {
	// Object productSubNode = i$.next();
	//
	// if (productSubNode instanceof ProductSectionType.Property) {
	// ProductSectionType.Property prop = (ProductSectionType.Property)
	// productSubNode;
	//
	// productProperties.add(prop);
	// }
	//
	// }
	//
	// return productProperties;
	// }
	// }
	//
	// }
	//
	// String errorMessage = "No ProductionSection with className "
	// + className + " has been found in VirtualSystem with id "
	// + vs.getId() + " in ovf readed from inputstream "
	// + this.inputStream.toString();
	//
	// logger.error(errorMessage);
	// throw new RuntimeException(errorMessage);
	// }
	//
}
