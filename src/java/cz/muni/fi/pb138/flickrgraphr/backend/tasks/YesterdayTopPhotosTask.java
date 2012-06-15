package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.TopPhotos;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to download (and delete) the day before yesterday top photos
 *
 * @author Jan Drabek
 */
public class YesterdayTopPhotosTask extends TaskInContext {

	private static final Logger logger = Logger.getLogger(YesterdayTopPhotosTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if (context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new TopPhotos(context);
		try {
			//entity.unload();
			entity.load();
			logger.log(Level.INFO, "Processing of 'top-photos' finished.");
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Downloading of 'top-photos' failed.", ex);
		}

	}
}