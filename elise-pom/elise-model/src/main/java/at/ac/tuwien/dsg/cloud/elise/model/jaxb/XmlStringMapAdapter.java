package at.ac.tuwien.dsg.cloud.elise.model.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// http://stackoverflow.com/questions/11329388/jaxb-mapping-for-a-map
// http://stackoverflow.com/questions/14899555/jaxb-attribute-with-object-type-throwing-null-pointer-exception
public class XmlStringMapAdapter extends XmlAdapter<MapElement, Map<String, String>> {

	@Override
	public MapElement marshal(Map<String, String> map) throws Exception {

		if (map == null || map.isEmpty()) {
			return null;
		}

		MapElement eMap = new MapElement();

		for (String key : map.keySet()) {
			eMap.addEntry(key, map.get(key));
		}

		return eMap;
	}

	@Override
	public Map<String, String> unmarshal(MapElement eMap) throws Exception {
		if (eMap == null) {
			return null;
		}

		Map<String, String> map = new HashMap<>(eMap.entries.size());

		for (EntryElement entry : eMap.entries) {
			map.put(entry.key, entry.value);
		}

		return map;
	}
}
