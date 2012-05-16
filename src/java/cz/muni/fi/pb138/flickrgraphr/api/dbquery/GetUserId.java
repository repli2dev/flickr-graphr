package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class GetUserId extends AbstractDatabaseQuery {
    
    	public GetUserId(ServletContext context) {
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
