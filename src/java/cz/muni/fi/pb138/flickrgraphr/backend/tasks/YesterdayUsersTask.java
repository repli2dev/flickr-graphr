package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.Users;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to download (and delete) user names for the day before yesterday top photos
 *
 * @author Jan Drabek
 */
public class YesterdayUsersTask extends TaskInContext {

	private static final Logger logger = Logger.getLogger(YesterdayUsersTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if (context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new Users(context);
		try {
			//entity.unload();
			entity.load();
			logger.log(Level.INFO, "Processing of 'users' (1 day before) finished.");
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Downloading of 'users' (1 day before) failed.", ex);
		}

	}
}