package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.basex.server.ClientSession;

/**
 * Implements some general methods for FlickrEntity
 *
 * @author Jan Drabek, Martin Ukrop
 */
public abstract class AbstractFlickrEntity implements FlickrEntity {
	// Options

	protected ServletContext context;
	protected BaseXSession database;
	protected String path;

	/**
	 * Returns connection to the database (if context is set it tries to
	 * gain it from there) (Database Session can be injected independently)
	 *
	 * @return
	 */
	protected BaseXSession getDatabaseSession() {
		if (database == null && context != null) {
			database = (BaseXSession) context.getAttribute(BaseXSession.BASE_X_SESSION);
		}
		return database;
	}

	/**
	 * Inject connection to the database
	 *
	 * @param database
	 */
	public void setDatabaseSession(BaseXSession database) {
		this.database = database;
	}

	/**
	 * Set path used for searching xsd, xq, etc.
	 *
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Return URL path to xsd, xq, etc.
	 *
	 * @param what
	 */
	protected URL getPath(String what) throws MalformedURLException {
		if (context != null) {
			return context.getResource(what);
		}
		return new URL(path + what);
	}

	/**
	 * Saves given stream data to specified database into specified file.
	 *
	 * @param dbName
	 * @param dbFileName
	 * @param data
	 * @throws FlickrEntityException
	 */
	protected void saveToDababase(String dbName, String dbFileName, InputStream data)
		throws FlickrEntityException {
		ClientSession session = null;
		try {
			session = getDatabaseSession().get(dbName, true);
		} catch (NoDatabaseException ex) {
			throw new FlickrEntityException("No database.", ex);
		}
		try {
			if (session.execute("XQUERY db:exists(\"" + dbName + "\",\"" + dbFileName + "\")").equals("true")) {
				session.execute("DELETE " + dbFileName);
			}
			session.add(dbFileName, data);
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot add data to the database '" + dbName + "'.", ex);
		} finally {
			try {
				session.close();
			} catch (IOException ex) {
				throw new FlickrEntityException("Cannot close connection to the database.", ex);
			}
		}
	}

	/**
	 * Deletes given file from given database
	 *
	 * @param dbName
	 * @param dbFileName
	 * @throws FlickrEntityException
	 */
	protected void deleteFromDatabase(String dbName, String dbFileName)
		throws FlickrEntityException {
		ClientSession session = null;
		try {
			session = getDatabaseSession().get(dbName, true);
		} catch (NoDatabaseException ex) {
			throw new FlickrEntityException("No database.", ex);
		}
		try {
			BaseXSession.enableWriteback(session);
			session.execute("DELETE " + dbFileName);
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot delete data from the database '" + dbName + "'.", ex);
		} finally {
			try {
				session.close();
			} catch (IOException ex) {
				throw new FlickrEntityException("Cannot close connection to the database.", ex);
			}
		}
	}

	/**
	 * Validates given xml against referenced XMLScheme. If the file does
	 * not validate, exception is raised.
	 *
	 * @param xml
	 * @param xmlSchema string path to local resource (e.g. "/xml/...")
	 * @param description for xml identification when exception is thrown
	 * @param saveIfError determines if the data should be saved to disk, if
	 * error occurs
	 * @throws FlickrEntityException
	 */
	protected void validateXML(String xml, String xmlSchema, String description, boolean saveIfError)
		throws FlickrEntityException {
		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		//File test = new File(context.getResource("/").getPath());
		try {
			Source source = new StreamSource(new StringReader(xml));
			Schema schema = sFactory.newSchema(getPath(xmlSchema));
			Validator validator = schema.newValidator();
			validator.validate(source);
		} catch (Exception ex) { //IOEXception, SAXException, MalformedURLException
			if (saveIfError) {
				try {
					URL url = null;
					if (context != null) {
						url = new URL("file://" + context.getRealPath("/xml/error") + "/" + DateTimeHelper.formatDateTime(DateTimeHelper.now()) + "_" + description + ".xml");
					} else {
						url = getPath("/xml/error/" + DateTimeHelper.formatDateTime(DateTimeHelper.now()) + "_" + description + ".xml");
					}
					PrintWriter errorOut = new PrintWriter(url.getPath());
					errorOut.println(xml);
					errorOut.close();
				} catch (IOException subEx) {
					throw new FlickrEntityException("Validation of " + description + " failed. (file not saved)", ex);
				}
				throw new FlickrEntityException("Validation of " + description + " failed. (file saved to xml/error)", ex);
			} else {
				throw new FlickrEntityException("Validation of " + description + " failed.", ex);
			}
		}
	}

	/**
	 * returns given String as InputStream
	 *
	 * @param input
	 * @return
	 */
	protected static InputStream getAsInputStream(String input) {
		byte[] barray = input.getBytes();
		return new ByteArrayInputStream(barray);
	}
	
	/**
	 * saves given String into file
	 * @param input
	 * @return 
	 */
	protected void dumpData(String input,String nameSuffix) throws Exception {
		if (context == null) return;
		URL url = new URL("file://" + context.getRealPath("/xml/error") + "/" + DateTimeHelper.formatDateTime(DateTimeHelper.now()) + "_" + this.getClass().getName() + "_"+ nameSuffix +".xml");
		System.err.println(url.getPath());
		PrintWriter errorOut = new PrintWriter(url.getPath());
		errorOut.println(input);
		errorOut.close();
	}
}
