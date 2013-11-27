package at.ac.tuwien.dsg.cloud.manifest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.manifest.xml.Property;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JSONManifestReader implements ManifestReader {

	private Logger logger = Logger.getLogger(JSONManifestReader.class);

	// INTERNAL
	private List<String> virtualSystemsStartupOrder = new ArrayList();

	private JsonObject json;
	private InputStream inputStream;
	private Gson gson;

	public JSONManifestReader(InputStream ovfInputStream) {

		gson = new Gson();
		JsonParser parser = new JsonParser();
		json = parser.parse(
				new JsonReader(new InputStreamReader(ovfInputStream)))
				.getAsJsonObject();
		try {
			ovfInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getEntryPoint() {
		try {
			return json.get("entry-point").getAsString();
		} catch (Exception e) {
			logger.info("Entry point is null ");
			return null;
		}
	}

	private Map<String, String> getData(JsonElement element, String prefix) {
		// If this is a regular primitive return, prefix+name as key, value as
		// value
		Map<String, String> partialResult = new HashMap<String, String>();

		if (element.isJsonPrimitive()) {
			// Remove trailing "."
			String key = prefix.substring(0, prefix.lastIndexOf("."));
			String value = element.getAsString();

			partialResult.put(key, value);
		} else {
			for (Entry<String, JsonElement> entry : element.getAsJsonObject()
					.entrySet()) {
				// Depth first
				String newPrefix = prefix.concat(entry.getKey()).concat(".");

				partialResult.putAll(getData(entry.getValue(), newPrefix));
			}
		}
		return partialResult;
	}

	@Override
	public List<Property> getProductSectionForVee(String veeName) {
		JsonObject vee = json.getAsJsonObject(veeName);

		List<Property> productSection = new ArrayList<Property>();
		Map<String, String> allData = new HashMap<String, String>();

		if (vee.getAsJsonObject("userdata") != null) {
			allData.putAll(getData(vee.getAsJsonObject("userdata"), "USERDATA:"));
		}

		// Maybe we should include also the getLaunchParameters

		for (String propertyName : allData.keySet()) {
			productSection.add(new Property(propertyName, allData
					.get(propertyName)));
		}
		return productSection;
	}

	// TODO This is really BAD but for the moment is the only way to obtain the
	// same results with the JSON/XML manifest
	public Map<String, String> getUserDataFromManifest(String veeName) {
		JsonObject vee = json.getAsJsonObject(veeName);

		Map<String, String> userData = new HashMap<String, String>();
		// STARTUP SECTION
		if (vee.get("start-up") != null) {
			JsonArray array = vee.getAsJsonArray("start-up");
			String[] strings = gson.fromJson(array.toString(), String[].class);
			StringBuffer sb = new StringBuffer();
			for (String component : Arrays.asList(strings)) {
				sb.append(component);
				sb.append(",");
			}
			// Remove trailing , if any
			try {
				sb.deleteCharAt(sb.lastIndexOf(","));
			} catch (Exception e) {
				logger.debug("", e);
			}
			userData.put("STARTUP", sb.toString());
		}

		if (vee.getAsJsonObject("userdata") != null) {
			userData.putAll(getData(vee.getAsJsonObject("userdata"),
					"USERDATA:"));
		}

		return userData;
	}

	public List<String> getVirtualSystemsStartupOrder() {
		JsonArray array = json.getAsJsonArray("roles-starup");
		String[] strings = gson.fromJson(array.toString(), String[].class);
		return Arrays.asList(strings);
	}

	public int getMinInstancesForVee(String veeName) {
		return json.getAsJsonObject(veeName).get("min-instance-count")
				.getAsInt();
	}

	public int getMaxInstancesForVee(String veeName) {
		return json.getAsJsonObject(veeName).get("max-instance-count")
				.getAsInt();
	}

	public int getInitInstancesForVee(String veeName) {
		return json.getAsJsonObject(veeName).get("initial-instance-count")
				.getAsInt();
	}

	@Override
	public Map<String, Object> getLaunchParameters(String veeName) {
		JsonObject vee = json.getAsJsonObject(veeName);

		Map<String, Object> launchParameters = new HashMap<String, Object>();

		launchParameters.put("INSTANCE_TYPE", vee.get("instance-type")
				.getAsString());
		launchParameters.put("BASE_EMI", vee.get("image-id").getAsString());

		// Not mandatory
		if (vee.get("ssh-key-name") != null) {
			launchParameters.put("SSH_KEY", vee.get("ssh-key-name")
					.getAsString());
		}
		if (vee.get("security-groups") != null) {
			launchParameters.put("SECURITY_GROUP", gson.fromJson(
					vee.get("security-groups").toString(), String[].class));
		}

		return launchParameters;
	}

}
