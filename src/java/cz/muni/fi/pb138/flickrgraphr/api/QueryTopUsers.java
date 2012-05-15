package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class QueryTopUsers extends AbstractDatabaseQuery {
    
    	public QueryTopUsers(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            return "top users here.";
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {

        }
}
