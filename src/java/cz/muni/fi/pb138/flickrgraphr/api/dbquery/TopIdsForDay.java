package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class TopIdsForDay extends AbstractDatabaseQuery {
    
    	public TopIdsForDay(ServletContext context) {
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
