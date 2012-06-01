package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.Date;

/**
 * Test task to check proper behaviour of TaskScheduler
 *
 * @author Jan Drabek
 */
public class TestTask extends TaskInContext {

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if (context != null) {
			System.out.println(context.getContextPath());
		}
		System.out.println("Current system time: " + new Date());
	}
}