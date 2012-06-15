package cz.muni.fi.pb138.flickrgraphr.backend.cron;

import it.sauronsoftware.cron4j.Task;
import javax.servlet.ServletContext;

/**
 * Task which can access ServletContext (e.g. for database etc) Context can be null!
 *
 * @author Jan Drabek
 */
public abstract class TaskInContext extends Task {

	protected ServletContext context;

	/**
	 * Inject ServletContext into task (DI pattern)
	 *
	 * @param context Context of web-app
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}
}
