package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * //TODO documentation
 * @author martin
 */
public class AddUser extends AbstractDatabaseQuery {
    
        // parameters for XQuery
        private String userId;
        private String displayName;
    
    	public AddUser(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            	// Get database
		ClientSession session = getDatabaseSession().get("users");
                BaseXSession.enableWriteback(session);
		// Get query
		String queryContent = getQuery("/xml/xquery/api_add_user.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("user_id", userId);
                        query.bind("display_name", displayName);
			query.bind("date", getYesterdayDate());
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
        
        private String getYesterdayDate() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = Calendar.getInstance().getTime();
            date.setTime(date.getTime()-1000-3600*24);
            return dateFormat.format(date);
        }
}
