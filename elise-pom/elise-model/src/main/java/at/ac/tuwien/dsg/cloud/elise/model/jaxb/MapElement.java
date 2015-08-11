package at.ac.tuwien.dsg.cloud.elise.model.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "map")
public class MapElement {

	@XmlElement(name = "entry")
	public List<EntryElement> entries = new ArrayList<EntryElement>();

	public void addEntry(String key, String value) {
		entries.add(new EntryElement(key, value));
	}

}