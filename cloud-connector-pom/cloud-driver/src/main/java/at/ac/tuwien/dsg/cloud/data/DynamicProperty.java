package at.ac.tuwien.dsg.cloud.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.manifest.utils.DeploymentPropertiesManagementUtils;

/**
 * 
 * @author bisignam@usi.ch (Mario Bisignani)
 * 
 * 
 *         This class acts as a container for all the information related to a
 *         dynamic property (a property for which the value is not known prior
 *         to service deployment) The class also exposes some static methods to
 *         check if a string represents a dynamic property and retrive other
 *         information
 * 
 */
public class DynamicProperty extends OvfProperty {

	private static Logger logger = Logger
			.getLogger(at.ac.tuwien.dsg.cloud.data.DynamicProperty.class);

	// The name of the VEE from which the property must be readed
	private String veeName;

	// The VEE replica from which we must read the property
	private Integer replicaNum;

	public static String dynamicPropPattern = "\\@\\(\\s*([a-zA-Z]+)\\s*,"
			+ "\\s*(\\d*)\\s*," + "\\s*([a-zA-Z]+)\\s*\\)";

	public DynamicProperty(String propString) {

		String errorMessage;

		Pattern pattern = Pattern.compile(dynamicPropPattern);

		Matcher matcher = pattern.matcher(propString);

		boolean matchFound = matcher.find();

		if (matchFound) {

			for (int i = 0; i <= matcher.groupCount(); i++) {
				logger.debug("DynamicProperty pattern matcher, group " + i
						+ " match is " + matcher.group(i));
			}

			// NOTE matcher.group(0) return the whole input String

			// Group 1 is the VEEname
			veeName = matcher.group(1);

			// Group 2 is the replicaNum
			replicaNum = Integer.parseInt(matcher.group(2));

			// Group 3 is the name of the property
			propertyName = matcher.group(3);

		} else {
			errorMessage = "The string "
					+ propString
					+ " given on input to the DynamicProperty constructor"
					+ " doesn't represent a valid dynamic property, a dynamic property must be defined in the following way: \n"
					+ "@( $VEEName, $replicaNum, $nameOfProperty)";

			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	}

	public static boolean isDynamicProperty(String propString) {
		try {
			logger.debug("Prop " + propString);
			Pattern pattern = Pattern.compile(dynamicPropPattern);

			Matcher matcher = pattern.matcher(propString);

			boolean matchFound = matcher.find();

			if (matchFound) {
				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getValue() throws IllegalArgumentException {

		String errorMessage;
		String methodName = null;
		Class aClass = null;
		// Object iClass;
		Method aMethod;
		Class params[] = { String.class, Integer.class };
		Object paramsObj[] = { veeName, replicaNum };

		try {
			aClass = Class
					.forName(DeploymentPropertiesManagementUtils.getDynamicPropsClass);

			// iClass = aClass.newInstance();

			// Retrieve the name of the method we need to use to retrieve the
			// value of the property
			methodName = DeploymentPropertiesManagementUtils.dynamicPropertyRetrievalMethods
					.get(this.propertyName);

			aMethod = aClass.getDeclaredMethod(methodName, params);

			return aMethod.invoke(aClass, paramsObj).toString();

		} catch (ClassNotFoundException e) {
			errorMessage = "Class "
					+ DeploymentPropertiesManagementUtils.getDynamicPropsClass
					+ " has not been found in the classpath, "
					+ "cannot use reflection to dynamically" + "call method "
					+ methodName + " and retrieve value of dynamic property "
					+ this.toString();

			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (SecurityException e) {
			errorMessage = "Cannot use reflection to access class "
					+ aClass.getName();
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (NoSuchMethodException e) {
			errorMessage = "Method " + methodName
					+ " has not been declared for class " + aClass.getName()
					+ ", impossible to retrieve value of dynamic property "
					+ this.toString();
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			errorMessage = "Method " + methodName
					+ " doesn't accept parameters " + paramsObj.toString() + ""
					+ ", impossible to retrieve value of dynamic property "
					+ this.toString();
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (IllegalAccessException e) {
			errorMessage = "Cannot use reflection to access class "
					+ aClass.getName();
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (InvocationTargetException e) {
			errorMessage = "Method " + methodName + " of class "
					+ aClass.getName() + " has throwed an exception";
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		}
		// } catch (InstantiationException e) {
		// errorMessage="Cannot create an instance of class "+aClass.getName()+", it is a non-instantiable class";
		// logger.error(errorMessage, e);
		// throw new IllegalArgumentException(errorMessage, e);
		// }

	}

	public String getVeeName() {
		return veeName;
	}

	public Integer getReplicaNum() {
		return replicaNum;
	}

	@Override
	public String toString() {
		return "veeName: " + veeName + ", replicaNum: " + replicaNum
				+ " property name: " + propertyName;
	}

}
