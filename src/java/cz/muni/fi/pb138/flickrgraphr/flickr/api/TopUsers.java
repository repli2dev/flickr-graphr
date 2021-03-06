package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents processing of top-photos->top-users It takes care of computing
 * daily data and its (de)storing into database Produces 'top-people' of the day
 *
 * @author Martin Ukrop
 */
public class TopUsers extends AbstractFlickrEntity {

	// Constants
	/**
	 * @var Name of database used for storing data
	 */
	private final String DATABASE = "top-users";
	// Inner data
	private String data;
	private String outputData;
	private String date;

	/**
	 * Create new TopUsers object with the date specified
	 *
	 * @param context
	 * @param date date to process
	 */
	public TopUsers(ServletContext context, String date) {
		this.context = context;
		this.date = date;
	}

	/**
	 * Create new TopUsers object and sets yesterday as date
	 *
	 * @param context
	 */
	public TopUsers(ServletContext context) {
		this.context = context;
		this.date = DateTimeHelper.formatDate(DateTimeHelper.yesterday());
	}

	@Override
	public void load() throws FlickrEntityException {
		computeData();
		//just double-checking, to preserve db consistency
		validateXML(outputData, "/xml/scheme/graphr_db_top_users.xsd",
			"graphr.top-users", true);
		saveToDababase(DATABASE, date, getAsInputStream(outputData));
	}

	@Override
	public void unload() throws FlickrEntityException {
		// Remove data older than 14 days
		deleteFromDatabase(DATABASE, DateTimeHelper.formatDate(DateTimeHelper.shiftDate(14 * 24 * 3600 * 1000)));
	}

	/**
	 * Computes the needed data according to class attribute settings
	 *
	 * @throws FlickrEntityException
	 */
	private void computeData() throws FlickrEntityException {
		ClientSession session = null;
		try {
			session = getDatabase();
		} catch (NoDatabaseException ex) {
			throw new FlickrEntityException("No database", ex);
		}
		try {
			String queryString = readFileToString(getPath("/xml/xquery/topusers_from_topphotos.xq"));
			ClientQuery cQuery = session.query(queryString);
			cQuery.bind("date_processed", date);
			cQuery.bind("max_delay", 7);
			this.outputData = cQuery.execute();
		} catch (MalformedURLException ex) {
			throw new FlickrEntityException("XQuery transformation of 'top-photos' to 'top-users' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XQuery transformation of 'top-photos' to 'top-users' failed.", ex);
		}
	}

	/**
	 * helper function to read file contents to String
	 *
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileToString(URL filePath) throws java.io.IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(filePath.openStream()));
		String line;
		String results = "";
		line = reader.readLine();
		while (line != null) {
			results += line + "\n";
			line = reader.readLine();
		}
		reader.close();
		return results;
	}

        /**
         * returns current database session
         * 
         * @return current ClientSession
         * @throws NoDatabaseException 
         */
	private ClientSession getDatabase() throws NoDatabaseException {
		BaseXSession bxs = getDatabaseSession();
		return bxs.get(DATABASE, true);
	}
}
