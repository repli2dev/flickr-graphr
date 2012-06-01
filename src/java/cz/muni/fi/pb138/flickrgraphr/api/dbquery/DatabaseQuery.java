package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;

/**
 * Abstraction over one Graphr API database query
 *
 * @author Martin Ukrop
 */
public interface DatabaseQuery {

	/**
	 * Executes a query with parameters set by 'setParameter'
	 *
	 * @return resulting query data (mostly JSON, see concrete
	 * implementations)
	 * @throws DatabaseQueryException
	 */
	String execute() throws DatabaseQueryException;

	/**
	 * Sets a parameter for query (for concrete parameters required, see
	 * documentation in implementations)
	 *
	 * @param name parameter name
	 * @param value parameter value
	 * @throws DatabaseQueryException
	 */
	void setParameter(String name, String value) throws DatabaseQueryException;

	/**
	 * Inject a database session to query class
	 *
	 * @param database
	 */
	void setDatabaseSession(BaseXSession database);
}
