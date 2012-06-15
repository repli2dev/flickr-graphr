package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents one DB query needed to process request on Graphr API Gets the
 * scores for given tag, score method determined by parameter
 *
 * @author Jan Drabek
 */
public class TopTags extends AbstractDatabaseQuery {

	private String tag;
	private String method;

	/**
	 * Create instance of query
	 * @param context 
	 */
	public TopTags(ServletContext context) {
		this.context = context;
	}

	@Override
	public String execute() throws DatabaseQueryException {
		// Get database
		ClientSession session;
		try {
			session = getDatabaseSession().get("tags-week");
		} catch (NoDatabaseException ex) {
			throw new DatabaseQueryException("No database connection", ex);
		}
		// Get query
		String queryContent = getQuery("/xml/xquery/api_top_tags.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("value", tag);
			query.bind("method", method);
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'top tags'.", ex);
		}
	}

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		if (name.equals("tag")) {
			tag = value;
		} else if (name.equals("method")) {
			method = value;
		} else {
			throw new DatabaseQueryException("Parameter " + name + "does not exist.");
		}
	}
}