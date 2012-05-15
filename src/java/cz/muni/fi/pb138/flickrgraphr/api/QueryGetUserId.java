package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class QueryGetUserId extends AbstractDatabaseQuery {
    
    	public QueryGetUserId(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            return "38177739@N05";
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {

        }
}
