package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

/**
 * Exception to be raised when error occurs in Graphr API functionality while
 * querying the database for data
 *
 * @author Martin Ukrop
 */
public class DatabaseQueryException extends Exception {

	public DatabaseQueryException() {
		super();
	}

	public DatabaseQueryException(String message) {
		super(message);
	}

	public DatabaseQueryException(Throwable cause) {
		super(cause);
	}

	public DatabaseQueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
