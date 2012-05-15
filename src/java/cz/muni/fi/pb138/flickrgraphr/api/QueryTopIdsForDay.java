package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class QueryTopIdsForDay extends AbstractDatabaseQuery {
    
    	public QueryTopIdsForDay(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            return "top ids here.";
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {

        }
}
