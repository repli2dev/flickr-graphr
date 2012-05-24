package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.Users;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to download (and delete) user names for yesterday's top photos
 * @author Jan Drabek
 */
public class UsersTask extends TaskInContext{
	
	private static final Logger logger = Logger.getLogger(UsersTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if(context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new Users(context);
		try {
			//entity.unload();
			entity.load();
			logger.log(Level.INFO, "Processing of 'users' finished.");
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Downloading of 'users' failed.", ex);
		}
		
	}
}