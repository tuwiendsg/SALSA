package at.ac.tuwien.dsg.cloud.manifest;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class TestJSONArray {

	public static void main(String[] args) {
		String array = "[ \"a\", \"b\"]";

		JsonObject arrayJS = new JsonObject();
		JsonArray ar = new JsonArray();

		JsonPrimitive elem = new JsonPrimitive("a");
		ar.add(elem);
		elem = new JsonPrimitive("b");
		ar.add(elem);

		arrayJS.add("test-array", ar);
		System.out.println("TestJSONArray.main() " + arrayJS.toString());

		// A complex object
		String jsonString = "{\"key1\":\"val1\",\"key2\":\"val2\",\"key3\": {\"subkey1\":\"subvalue1\",\"subkey2\":\"subvalue2\"},\"key4\":\"val3\"," +
				"\"key5\":[\"a\", \"b\"]" +
				"" +
				"}";

		Map<String, Object> map = new Gson().fromJson(jsonString,
				new TypeToken<Map<String, Object>>() {
				}.getType());

		System.out.println(" Map " + map);
		System.out.println("TestJSONArray.main() " + map.get("key5").getClass());
		
		JsonParser parser = new JsonParser();
		JsonObject theObject = parser.parse( jsonString ).getAsJsonObject();
		
		System.out.println("TestJSONArray.main() theObject " + theObject );

	}
}
