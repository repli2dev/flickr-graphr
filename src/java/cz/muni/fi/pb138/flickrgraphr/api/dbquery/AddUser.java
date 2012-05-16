package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import javax.servlet.ServletContext;

/**
 * //TODO documentation
 * @author martin
 */
public class AddUser extends AbstractDatabaseQuery {
    
    	public AddUser(ServletContext context) {
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
