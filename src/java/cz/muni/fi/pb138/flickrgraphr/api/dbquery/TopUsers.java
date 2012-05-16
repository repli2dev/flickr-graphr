package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import javax.servlet.ServletContext;
import org.basex.server.ClientSession;

/**
 * //TODO documentation
 *
 * @author martin
 */
public class TopUsers extends AbstractDatabaseQuery {

	public TopUsers(ServletContext context) {
		this.context = context;
	}

	@Override
	public String execute() throws DatabaseQueryException {
		return "top users here.";
	}

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		
	}
}
