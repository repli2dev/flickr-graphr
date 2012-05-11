package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;

/**
 *
 * @author Jan Drabek
 */
public abstract class AbstractFlickrEntity implements FlickrEntity {
	// Options
	protected ServletContext context;
	protected BaseXSession database;
	protected String path;
	protected String type;
	
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
}
