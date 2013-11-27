package at.ac.tuwien.dsg.cloud.data;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.manifest.utils.DeploymentPropertiesManagementUtils;

public class StaticProperty extends OvfProperty {

	private static Logger logger = Logger
			.getLogger(at.ac.tuwien.dsg.cloud.data.StaticProperty.class);

	// Pattern for retrieving a static property information
	public static final String staticPropPattern = "\\#\\(\\s*([a-zA-Z]+)\\s*\\)";

	public StaticProperty(String propString) {

		String errorMessage;

		Pattern pattern = Pattern.compile(staticPropPattern);

		Matcher matcher = pattern.matcher(propString);

		boolean matchFound = matcher.find();

		if (matchFound) {

			for (int i = 0; i <= matcher.groupCount(); i++) {
				logger.debug("StaticProperty pattern matcher, group " + i
						+ " match is " + matcher.group(i));
			}

			// NOTE matcher.group(0) return the whole input String

			// Group 3 is the name of the property
			propertyName = matcher.group(1);

		} else {
			errorMessage = "The string "
					+ propString
					+ " given on input to the StaticProperty constructor"
					+ " doesn't represent a valid static property, a static property must defined in the following way: \n"
					+ "#( nameOfProperty )";

			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

	}

	public static boolean isStaticProperty(String propString) {
		try {
			Pattern pattern = Pattern.compile(staticPropPattern);

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

	@SuppressWarnings("rawtypes")
	public String getValue() {

		String errorMessage;
		Field field = null;
		String value;

		Class aClass = DeploymentPropertiesManagementUtils.class;

		try {
			field = aClass.getField("ovf_value_" + propertyName);
			value = (String) field.get(null);
		} catch (NoSuchFieldException e) {
			errorMessage = "The static property name "
					+ propertyName
					+ " is not associated with any field in the"
					+ aClass.getCanonicalName()
					+ " class "
					+ "if you need to use new static values in the manifest please define a new public static final "
					+ "field in the "
					+ aClass.getCanonicalName()
					+ " class and name it ovf_value_$PROP_NAME where $PROP_NAME is the name of the new static property"
					+ "you want to use in the manifest";
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			errorMessage = "Cannot access to value of field " + field.getName()
					+ " of class " + aClass.getName();
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		} catch (IllegalAccessException e) {
			errorMessage = "Cannot access to field " + field.getName()
					+ " of class " + aClass.getName();
			;
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		}

		return value;

	}

}
