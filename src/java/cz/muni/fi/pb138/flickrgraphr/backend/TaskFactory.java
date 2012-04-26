package cz.muni.fi.pb138.flickrgraphr.backend;

import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * Create task
 * @author Jan Drabek
 */
public class TaskFactory {
	private static final Logger logger = Logger.getLogger(TaskFactory.class.getName());
	
	private static ServletContext context = null;

	/**
	 * Inject ServletContext
	 * @param context Context
	 */
	public static void setContext(ServletContext context) {
		TaskFactory.context = context;
	}
	/**
	 * Factory pattern
	 */
	private TaskFactory() {}
	
	/**
	 * Try to instantiate all tasks from conf with proper scheduling. Return null if not services available.
	 * @return All collected tasks
	 */
	public static TaskCollector collectTasks(Configuration conf) {
		SimpleTaskCollector collector = new SimpleTaskCollector();
		Class<Task> temp;
		Task temp2;
		if(conf != null) {
			for (Map.Entry<TaskName, String> entry : conf.getEntries().entrySet()) {
				TaskName name = entry.getKey();
				String time = entry.getValue();
				logger.log(Level.FINE, "Adding to schedule task {0} with timing {1}", new Object[]{name.getName(), time});
				try {
					temp = (Class<Task>) Class.forName(name.getName());
					temp2 = temp.newInstance();
					((TaskInContext) temp2).setContext(context);
					collector.addTask(new SchedulingPattern(time),temp2);
				} catch (IllegalAccessException ex) {
					logger.log(Level.INFO,"Cannot access task class (check if public).", ex);
				} catch (ClassNotFoundException ex) {
					logger.log(Level.INFO,"Task class not found.", ex);
				} catch (InstantiationException ex) {
					logger.log(Level.INFO,"Cannot create instance of task.", ex);
				}
			}
		}
		return ((TaskCollector) collector);
	}
}
