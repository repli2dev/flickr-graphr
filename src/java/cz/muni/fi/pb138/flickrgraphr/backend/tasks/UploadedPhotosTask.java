package cz.muni.fi.pb138.flickrgraphr.backend.tasks;

import cz.muni.fi.pb138.flickrgraphr.backend.cron.TaskInContext;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.UploadedPhotos;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to download count of uploaded photos
 *
 * @author Jan Drabek
 */
public class UploadedPhotosTask extends TaskInContext {

	private static final Logger logger = Logger.getLogger(UploadedPhotosTask.class.getName());

	@Override
	public void execute(TaskExecutionContext tec) throws RuntimeException {
		if (context == null) {
			throw new RuntimeException("No context, cannot continue;");
		}
		FlickrEntity entity = new UploadedPhotos(context, DateTimeHelper.yesterday());
		try {
			entity.load();
			logger.log(Level.INFO, "Processing of 'count of uploaded photos' finished.");
		} catch (FlickrEntityException ex) {
			logger.log(Level.SEVERE, "Downloading of 'count of uploaded photos' failed.", ex);
		}

	}
}