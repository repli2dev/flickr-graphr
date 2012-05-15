package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class QueryAddUser extends AbstractDatabaseQuery {
    
    	public QueryAddUser(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            return "user successfully added.";
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {

        }
}
