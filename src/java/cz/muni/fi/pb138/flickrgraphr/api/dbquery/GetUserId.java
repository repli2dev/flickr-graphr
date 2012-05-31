package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * //TODO documentation
 * @author martin
 */
public class GetUserId extends AbstractDatabaseQuery {
    
        private String displayName;
    
    	public GetUserId(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
                // Get database
		ClientSession session;
		try {
			session = getDatabaseSession().get("users");
		} catch (NoDatabaseException ex) {
			throw new DatabaseQueryException("No database connection", ex);
		}
		// Get query
		String queryContent = getQuery("/xml/xquery/api_get_user_id.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("requested_display_name", displayName);
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'get user id'.", ex);
		}
        }

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		if (name.equals("displayName")) {
                    displayName = value;
                } else {
                    throw new DatabaseQueryException("Parameter " + name + "does not exist.");
                }
	}
}
