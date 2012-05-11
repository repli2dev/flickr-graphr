package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.HotList;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to download (and delete) week hot list
 * @author Jan Drabek
 */
public class WeekHotListTask extends TaskInContext{
	
	private static final Logger logger = Logger.getLogger(WeekHotListTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if(context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new HotList(context, "week");
		try {
			entity.unload();
			entity.load();
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Downloading of 'week hot list' failed.", ex);
		}
		
	}
}