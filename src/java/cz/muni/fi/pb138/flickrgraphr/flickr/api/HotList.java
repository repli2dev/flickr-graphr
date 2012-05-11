package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.basex.server.ClientSession;
import org.xml.sax.SAXException;

/**
 * Reprezents one page of Flickr API - Hot list
 * It take care downloading, validation and transforming the recieved data and its (de)storing into database
 * @author Jan Drabek
 */
public class HotList extends AbstractFlickrEntity {
	
	// Constants
	/** @var URL for query with placeholders */
	private final String URL = "http://api.flickr.com/services/rest/?method=flickr.tags.getHotList&api_key=<!--API_KEY-->&period=<!--TYPE-->&count=100&format=rest";
	/** @var Name of database used for storing data */
	private final String DATABASE = "hot-list";	
	
	// Inner data
	private String data;
	private String outputData;

	/**
	 * Create new hot list object
	 * @param database Database session
	 * @param type week | day
	 */
	public HotList(ServletContext context, String type) {
		this.context = context;
		this.type = type;
	}
	

	@Override
	public void load() throws FlickrEntityException {
		get();
		validate();
		transform();
		// Insert data into database
		ClientSession session = getDatabase();
		try {
			if(session.execute("XQUERY db:exists(\"" + DATABASE + "\",\"" + getCurrentDate() + "\")").equals("true")) {
				session.execute("DELETE " + getCurrentDate());
			}
			session.add(getCurrentDate(), getOutputAsInputStream());
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot add data of 'hot list' to the database.", ex);
		} finally {
			try {
				session.close();
			} catch (IOException ex) {
				throw new FlickrEntityException("Cannot close connection to the database.", ex);
			}
		}
	}

	@Override
	public void unload() throws FlickrEntityException {
		// Remove data older than 14 days
		ClientSession session = getDatabase();
		try {
			BaseXSession.enableWriteback(session);
			session.execute("DELETE " + getOldDate());
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot delete data of 'hot list' to the database.", ex);
		} finally {
			try {
				session.close();
			} catch (IOException ex) {
				throw new FlickrEntityException("Cannot close connection to the database.", ex);
			}
		}
	}
	
	private InputStream getOutputAsInputStream() {
		byte[] barray = outputData.getBytes();
		return new ByteArrayInputStream(barray); 
	}
	
	private String getUrl() {
		// Prepare URL and fetch result
		String finalURL = URL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
		finalURL = finalURL.replaceAll("<!--TYPE-->", type);
		return finalURL;
	}
	
	private void get() throws FlickrEntityException {
		try {
			data = Downloader.download(getUrl());
		} catch (DownloaderException ex) {
			throw new FlickrEntityException("Downloading of 'hot list' failed.", ex);
		}
	}
	
	private void validate() throws FlickrEntityException {
		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Source source = new StreamSource(new StringReader(data));
			Schema schema = null;
			schema = sFactory.newSchema(getPath("/XML/scheme/flickr_api_hot_list.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(source);
		} catch (IOException ex) {
			throw new FlickrEntityException("Validation of downloaded 'hot list' failed (IO problems).", ex);
		} catch (SAXException ex) {
			throw new FlickrEntityException("Validation of downloaded 'hot list' failed (see if API key is set properly and quota is ok).", ex);
		}
	}
	
	private void transform() throws FlickrEntityException {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Source source = new StreamSource(new StringReader(data));
                Source xslt = null;
		try {
			xslt = new StreamSource(getPath("/XML/xslt/flickr_hotlist_to_tags_by_day.xslt").openStream());
		} catch (MalformedURLException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'hot list' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'hot list' failed.", ex);
		}
		StringWriter outputData = new StringWriter();
		Result result = new StreamResult(outputData);
		Transformer transformer;
		try {
			transformer = tfactory.newTransformer(xslt);
			transformer.setParameter("DATE", getCurrentDate());
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new FlickrEntityException("XSLT transformation of downloaded 'hot list' failed (check the input).");
		}
		this.outputData = outputData.toString();
	}
	
	private String getCurrentDate() {
		return formatDate(now());
	}
	
	private String getOldDate() {
		Date date = now();
		date.setTime(date.getTime()-24*3600*14);
		return formatDate(date);
	}
	
	private ClientSession getDatabase() {
		BaseXSession bxs = getDatabaseSession();
		return bxs.get(DATABASE, true);
	}

}
