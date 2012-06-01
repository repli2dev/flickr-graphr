package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents one DB query needed to process request on Graphr API Adds
 * userId/displayName pair to the database Default location is yesterday's data,
 * if not existent, uses the most recent existing date
 *
 * @author Martin Ukrop
 */
public class AddUser extends AbstractDatabaseQuery {

	// parameters for XQuery
	private String userId;
	private String displayName;

	/**
	 * Create instance of representation of DB query for adding user with given context
	 * @param context 
	 */
	public AddUser(ServletContext context) {
		this.context = context;
	}

	@Override
	public String execute() throws DatabaseQueryException {
		// Get database
		ClientSession session = null;
		try {
			session = getDatabaseSession().get("users");
		} catch (NoDatabaseException ex) {
			throw new DatabaseQueryException("No database connection", ex);
		}
		BaseXSession.enableWriteback(session);
		// Get query
		String queryContent = getQuery("/xml/xquery/api_add_user.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("user_id", userId);
			query.bind("display_name", displayName);
			query.bind("date", DateTimeHelper.formatDate(DateTimeHelper.yesterday()));
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'add user'.", ex);
		}
	}

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		if (name.equals("userId")) {
			userId = value;
		} else if (name.equals("displayName")) {
			displayName = value;
		} else {
			throw new DatabaseQueryException("Parameter " + name + "does not exist.");
		}
	}
}
