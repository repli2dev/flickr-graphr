package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class QueryTopTags extends AbstractDatabaseQuery {
    
    	public QueryTopTags(ServletContext context) {
		this.context = context;
	}
    
        @Override
        public String execute() throws DatabaseQueryException{
            return "top tags here.";
        }

        @Override
        public void setParameter(String name, String value) throws DatabaseQueryException {

        }
}