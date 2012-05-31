package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents one DB query needed to process request on Graphr API
 * Gets the required amount of best user IDs for given day
 * @author martin
 */
public class TopIdsForDay extends AbstractDatabaseQuery {
    
        // parameters for XQuery
        private String date;
        private int count;
    
    	public TopIdsForDay(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
		// Get database
		ClientSession session;
		try {
			session = getDatabaseSession().get("top-users");
		} catch (NoDatabaseException ex) {
			throw new DatabaseQueryException("No database connection", ex);
		}
		// Get query
		String queryContent = getQuery("/xml/xquery/api_top_users.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("date", date);
                        query.bind("count", count);
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'top users'.", ex);
		}
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {
		if (name.equals("date")) {
                    date = value;
                } else if (name.equals("count")) {
                    count = Integer.parseInt(value);
                } else {
                    throw new DatabaseQueryException("Parameter " + name + "does not exist.");
                }
        }
}
