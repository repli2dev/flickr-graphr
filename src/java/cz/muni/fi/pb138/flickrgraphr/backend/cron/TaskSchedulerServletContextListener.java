package cz.muni.fi.pb138.flickrgraphr.backend.cron;

import it.sauronsoftware.cron4j.Scheduler;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


/**
 * Take care about initialization and proper deinit of task scheduler.
 * @author Jan Drabek
 */
public class TaskSchedulerServletContextListener implements ServletContextListener {
	private boolean schedulerRunning = false;
	
	final static String SCHEDULER_NAME = "scheduler.cron4j";
	
	private static final Logger logger = Logger.getLogger(TaskSchedulerServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		// Get and parse configuration
		Configuration conf = null;
		logger.log(Level.FINE,"Loading scheduler configuration.");
		try {
			conf = Configuration.loadConfiguration(new File(context.getRealPath("/") + "/WEB-INF/scheduler.xml").toURI(),context.getResource("/XML/scheme/scheduler.xsd"));
		} catch (ParserConfigurationException ex) {
			logger.log(Level.SEVERE,"Scheduler configuration parser failed (wrong config).", ex);
		} catch (SAXException ex) {
			logger.log(Level.SEVERE,"Scheduler configuration parser failed (SAX).", ex);
		} catch (IOException ex) {
			logger.log(Level.SEVERE,"File with scheduler entries was not found or openable.", ex);
		}
		if(conf == null) {
			return;
		}
		// Create scheduler
		logger.log(Level.FINE,"Setting up scheduler.");
		Scheduler scheduler = new Scheduler();
		TaskFactory.setContext(context);
		scheduler.addTaskCollector(TaskFactory.collectTasks(conf));
		scheduler.start();
		context.setAttribute(SCHEDULER_NAME, scheduler);
		schedulerRunning = true;
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(!schedulerRunning) {
			return;
		}
		logger.log(Level.FINE,"Destroying scheduler.");
		ServletContext context = event.getServletContext();
		Scheduler scheduler = (Scheduler) context.getAttribute(SCHEDULER_NAME);
		context.removeAttribute(SCHEDULER_NAME);
		scheduler.stop();
	}

}
