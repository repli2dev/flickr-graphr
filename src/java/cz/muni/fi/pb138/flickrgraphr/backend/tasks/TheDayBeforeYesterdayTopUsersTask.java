package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.TopUsers;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to compute (and delete) top users for the day before yesterday
 *
 * @author Jan Drabek
 */
public class TheDayBeforeYesterdayTopUsersTask extends TaskInContext {

	private static final Logger logger = Logger.getLogger(TheDayBeforeYesterdayTopUsersTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if (context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new TopUsers(context,DateTimeHelper.formatDate(DateTimeHelper.shiftDate(2*1000*24*3600)));
		try {
			//entity.unload();
			entity.load();
			logger.log(Level.INFO, "Processing of 'top-users' finished.");
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Computing of 'top-users' failed.", ex);
		}

	}
}