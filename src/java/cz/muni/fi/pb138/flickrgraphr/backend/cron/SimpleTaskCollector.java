package cz.muni.fi.pb138.flickrgraphr.backend.cron;

import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskTable;

/**
 * Collector of tasks which are given to scheduler.
 * @author Jan Drabek
 */
public class SimpleTaskCollector implements TaskCollector {
	
	TaskTable tasks = new TaskTable();
	
	/**
	 * Add task
	 * @param time Cron-like scheduling pattern
	 * @param task Instance of class to schedule
	 */
	public void addTask(SchedulingPattern time, Task task) {
		tasks.add(time, task);
	}

	@Override
	public TaskTable getTasks() {
		return tasks;
	}
	
}
