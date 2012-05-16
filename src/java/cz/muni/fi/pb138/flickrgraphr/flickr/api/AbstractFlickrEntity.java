package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
 * @author Jan Drabek, Martin Ukrop
 */
public abstract class AbstractFlickrEntity implements FlickrEntity {
	// Options
	protected ServletContext context;
	protected BaseXSession database;
	protected String path;
	
	/**
	 * Returns connection to the database (if context is set it tries to gain it from there)
	 * (Database Session can be injected independently)
	 * @return 
	 */
	protected BaseXSession getDatabaseSession() {
		if(database == null && context != null) {
			database = (BaseXSession) context.getAttribute(BaseXSession.BASE_X_SESSION);
		}
		return database;
	}
        
	/**
	 * Inject connection to the database
	 * @param database 
	 */
	protected void setDatabaseSession(BaseXSession database) {
		this.database = database;
	}
	
	/**
	 * Set path used for searching xsd, xq, etc.
	 * @param path
	 */
	protected void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Return URL path to xsd, xq, etc.
	 * @param what
	 */
	protected URL getPath(String what) throws MalformedURLException {
		if(context != null) {
			return context.getResource(what);
		}
		return new URL(path + what);
	}
        
        /**
         * Saves given stream data to specified database into specified file.
         * @param dbName
         * @param dbFileName    
         * @param data
         * @throws FlickrEntityException 
         */
        protected void saveToDababase(String dbName, String dbFileName, InputStream data)
                                      throws FlickrEntityException {
                ClientSession session = getDatabaseSession().get(dbName, true);
		try {
			if(session.execute("XQUERY db:exists(\"" + dbName + "\",\"" + dbFileName + "\")").equals("true")) {
				session.execute("DELETE " + dbFileName);
			}
			session.add(dbFileName, data);
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot add data to the database '"+dbName+"'.", ex);
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
         * @param dbName
         * @param dbFileName
         * @throws FlickrEntityException 
         */
        protected void deleteFromDatabase(String dbName, String dbFileName) 
                                          throws FlickrEntityException {
                ClientSession session = getDatabaseSession().get(dbName, true);
		try {
			BaseXSession.enableWriteback(session);
			session.execute("DELETE " + dbFileName);
		} catch (IOException ex) {
			throw new FlickrEntityException("Cannot delete data from the database '"+dbName+"'.", ex);
		} finally {
			try {
				session.close();
			} catch (IOException ex) {
				throw new FlickrEntityException("Cannot close connection to the database.", ex);
			}
		}
        }
        
        /**
         * Validates given xml against referenced XMLScheme.
         * If the file does not validate, exception is raised.
         * @param xml
         * @param xmlSchema     string path to local resource (e.g. "/xml/...")
         * @param description   for xml identification when exception is thrown
         * @throws FlickrEntityException 
         */
        protected void validateXML(String xml, String xmlSchema, String description) throws FlickrEntityException {
		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Source source = new StreamSource(new StringReader(xml));
			Schema schema = null;
			schema = sFactory.newSchema(getPath(xmlSchema));
			Validator validator = schema.newValidator();
			validator.validate(source);
		} catch (Exception ex) { //IOEXception, SAXException, MalformedURLException
                        try {
                            URL url = getPath("/xml/error/"+formatDateTime(now()) +"_"+description+".xml");
                            PrintWriter errorOut = new PrintWriter(url.getPath());
                            errorOut.println(xml);
                            errorOut.close();
                        } catch (IOException subEx) {
                            throw new FlickrEntityException("Validation of "+description+" failed. (file not saved)", ex);
                        }
                        throw new FlickrEntityException("Validation of "+description+" failed. (file saved to xml/error)", ex);
		}
	}
        
        /**
         * returns given String as InputStream
         * @param input
         * @return 
         */
        protected static InputStream getAsInputStream(String input) {
		byte[] barray = input.getBytes();
		return new ByteArrayInputStream(barray); 
	}
        
	/**
	 * Return current date
	 * @return Current date
	 */
	protected Date now() {
		return Calendar.getInstance().getTime();
	}
	
	/**
	 * Return date in YYYY-MM-DD format
	 * @param date
	 * @return  Date formatted as string
	 */
	protected String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
        
        protected String formatDateTime(Date date) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                return dateFormat.format(date);
        }
}
