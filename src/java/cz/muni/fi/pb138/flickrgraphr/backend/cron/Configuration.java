package cz.muni.fi.pb138.flickrgraphr.backend.cron;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parse configuration of task scheduler from given path to XML file (@see scheduler.xsd)
 * @author jan
 */
public class Configuration {
	private Map<TaskName,String> conf;
	
	/**
	 * Created by factory pattern
	 * @param conf Entries from configuration
	 */
	private Configuration(Map<TaskName,String> conf) {
		this.conf = conf;
	}
	
	/**
	 * Return parsed configuration from path to XML file. Expect file to be a valid XML.
	 * @param uri URI to XML file (use path with context)
	 * @return Parsed configuration
	 * @throws ParserConfigurationException If parser could not be created due configuration problems
	 * @throws SAXException If parsing failed
	 * @throws IOException  If any I/O error is done
	 */
	public static Configuration loadConfiguration(URI uri, URL schemaLocation) throws ParserConfigurationException, SAXException, IOException{
		// Get DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// FIXME: Ensure that only valid XML are processed
		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sFactory.newSchema(schemaLocation);
		Source source = new StreamSource(new FileReader(uri.getPath()));
		schema.newValidator().validate(source);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(uri.toString());
		// Parse the string and get it into internal format
		Map<TaskName,String> temp = new HashMap<TaskName,String>();
		NodeList tasks = doc.getElementsByTagName("task");
		for(int i = 0; i < tasks.getLength(); i++) {
			Element task = (Element) tasks.item(i);
			NodeList name = task.getElementsByTagName("name");
			NodeList time = task.getElementsByTagName("time");
			temp.put(new TaskName(Integer.parseInt(task.getAttribute("id")), name.item(0).getTextContent()), time.item(0).getTextContent());
		}
		return new Configuration(temp);
	}
	/**
	 * Return all entries (Name of Task and cron-like timing)
	 * @return All configuration entries
	 */
	public Map<TaskName,String> getEntries() {
		return Collections.unmodifiableMap(conf);
	}
}