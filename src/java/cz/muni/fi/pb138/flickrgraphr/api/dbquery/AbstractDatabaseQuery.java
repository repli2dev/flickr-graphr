package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.tools.IOHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.ServletContext;

/**
 * Implements some general methods for DatabaseQuery
 *
 * @author Martin Ukrop, Jan Drabek
 */
public abstract class AbstractDatabaseQuery implements DatabaseQuery {

	protected ServletContext context;
	protected BaseXSession dbSession;
	protected String path;

	/**
	 * Returns connection to the database (if context is set it tries to
	 * gain it from there) (Database Session can be injected independently)
	 *
	 * @return
	 */
	protected BaseXSession getDatabaseSession() throws DatabaseQueryException {
		if (dbSession == null && context != null) {
			dbSession = (BaseXSession) context.getAttribute(BaseXSession.BASE_X_SESSION);
		}
		if (dbSession == null && context == null) {
			throw new DatabaseQueryException("Cannot get database - neither DB nor context is set.");
		}
		return dbSession;
		/**
		 * Inject connection to the database
		 *
		 * @param database
		 */
	}

	@Override
	public void setDatabaseSession(BaseXSession database) {
		this.dbSession = database;
	}

	/**
	 * Set path used for searching xsd, xq, etc.
	 *
	 * @param path
	 */
	protected void setPath(String path) {
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

	protected String getQuery(String name) throws DatabaseQueryException {
		URL pathToXQuery = null;
		try {
			pathToXQuery = getPath(name);
		} catch (MalformedURLException ex) {
			throw new DatabaseQueryException("Cannot load XQuery (wrong URL).", ex);
		}
		try {
			return IOHelper.fileToString(pathToXQuery.openStream());
		} catch (IOException ex) {
			throw new DatabaseQueryException("Cannot load XQuery (IO problems).", ex);
		}
	}
}
