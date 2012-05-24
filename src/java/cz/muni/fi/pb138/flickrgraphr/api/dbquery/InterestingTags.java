package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents one DB query needed to process request on Graphr API
 * Gets the interesting tags (with multiple days entries)
 * @author Jan Drabek
 */
public class InterestingTags extends AbstractDatabaseQuery {

	private int start;
	private int records;
	
	public InterestingTags(ServletContext context) {
		this.context = context;
	}

	@Override
	public String execute() throws DatabaseQueryException {
		// Get database
		ClientSession session;
		try {
			session = getDatabaseSession().get("tags-week");
		} catch (NullPointerException ex) {
			throw new DatabaseQueryException("No database connection");
		}
		// Get query
		String queryContent = getQuery("/xml/xquery/api_interesting_tags.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("start", start);
			query.bind("records", records);
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'interesting tags'.", ex);
		}
	}

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		int temp = 0;
		if (name.equals("start")) {
			temp = Integer.parseInt(value);
			if(temp < 0) {
				throw new DatabaseQueryException("Parameter start must be greater or equal to 0.");	
			}
			start = temp;
		} else if (name.equals("records")) {
			temp = Integer.parseInt(value);
			if(temp < 0 || temp > 100) {
				throw new DatabaseQueryException("Parameter records must be between 0 and 100.");	
			}
			records = temp;
		} else {
			throw new DatabaseQueryException("Parameter " + name + "does not exist.");
		}
	}
}