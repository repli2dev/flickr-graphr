package cz.muni.fi.pb138.flickrgraphr.backend.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.server.ClientSession;

/**
 * Class for providing connections (= working sessions) for BaseX database
 * Credentials to database are given in constructor
 *
 * @author Jan Drabek
 */
public class BaseXSession {

	/**
	 * @var Identification for storing into context variables
	 */
	public final static String BASE_X_SESSION = "BASE_X_SESSION";
	/**
	 * Credentials
	 */
	private String hostname;
	private int port;
	private String username;
	private String password;
	private static final Logger logger = Logger.getLogger(BaseXSession.class.getName());

	/**
	 * Create instance with given credentials
	 *
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 */
	public BaseXSession(String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * Return working session or null on error
	 */
	public ClientSession get() throws NoDatabaseException {
		ClientSession session = null;
		try {
			session = new ClientSession(hostname, port, username, password);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot establish connection to the database", ex);
			throw new NoDatabaseException();
		}
		logger.log(Level.FINE, "Connection to database established.");
		return session;
	}

	/**
	 * Return working session with opened database (won't be created if not
	 * existing)
	 *
	 * @param database Database to open
	 * @return Working session
	 */
	public ClientSession get(String database) throws NoDatabaseException {
		return get(database, false);
	}

	/**
	 * Return working session with opened database (create new one only if
	 * create is true)
	 *
	 * @param database Database to open/create
	 * @param create True means create if not exists
	 */
	public ClientSession get(String database, boolean create) throws NoDatabaseException {
		ClientSession session = get();
		try {
			// Check if database exists. Create only if wanted
			if (session.execute("XQUERY db:exists('" + database + "')").equals("false") && create) {
				session.create(database, new FileInputStream("/dev/null"));
			}
			// Open database
			session.execute("OPEN " + database);
			logger.log(Level.FINE, "Database opened");
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot work with database.", ex);
		}
		return session;
	}

	/**
	 * Enable writeback on given connection (expects not null session)
	 * @param session Session on which writeback should be enabled
	 */
	public static void enableWriteback(ClientSession session) {
		try {
			if (session.execute("GET WRITEBACK").equals("WRITEBACK: false\n")) {
				session.execute("SET WRITEBACK true");
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot enable writeback.", ex);
		}
	}

	/**
	 * Disable writeback on given connection (expects not null session)
	 * @param session Session on which writeback should be disabled
	 */
	public static void disableWriteback(ClientSession session) {
		try {
			if (session.execute("GET WRITEBACK").equals("WRITEBACK: true\n")) {
				session.execute("SET WRITEBACK false");
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot disable writeback.", ex);
		}
	}
}
