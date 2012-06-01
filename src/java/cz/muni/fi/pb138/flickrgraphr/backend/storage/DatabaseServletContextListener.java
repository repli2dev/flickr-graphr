package cz.muni.fi.pb138.flickrgraphr.backend.storage;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Take care about initialization database
 *
 * @author Jan Drabek
 */
public class DatabaseServletContextListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(DatabaseServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		Properties properties = null;
		try {
			properties = new Properties();
			properties.loadFromXML(context.getResourceAsStream("/WEB-INF/BaseXConf.xml"));
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot read database credentials.", ex);
		}
		BaseXSession database = new BaseXSession(properties.getProperty("hostname"), Integer.valueOf(properties.getProperty("port")), properties.getProperty("username"), properties.getProperty("password"));
		context.setAttribute(BaseXSession.BASE_X_SESSION, database);
		logger.log(Level.FINE, "Initialization of database completed.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
