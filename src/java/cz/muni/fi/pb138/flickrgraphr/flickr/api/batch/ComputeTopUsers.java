package cz.muni.fi.pb138.flickrgraphr.flickr.api.batch;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.AbstractFlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.TopUsers;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.text.ParseException;
import java.util.Date;

/**
 * Recomputes top-users data for a given date range and saves it to DB
 * Prior to running, ROOT_PATH must be set correctly
 * 
 * @author Martin Ukrop
 */
public class ComputeTopUsers {

        // setting for dates for which to get data (format YYYY-MM-DD)
	private static final String BEGIN_DATE = "2012-05-14";
	private static final String END_DATE = "2012-06-10";
        
	// setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/jan/TEMP/flickr-graphr/src";

	public static void main(String[] args) throws ParseException {
		BaseXSession database = new BaseXSession("localhost", 1984, "admin", "bastlix24");

		Date date = DateTimeHelper.parseDate(BEGIN_DATE);
		Date endDate = DateTimeHelper.parseDate(END_DATE);

		while (!date.after(endDate)) {
			String stringDate = DateTimeHelper.formatDate(date);
			FlickrEntity entity = new TopUsers(null, stringDate);
			((AbstractFlickrEntity) entity).setPath(ROOT_PATH);
			((AbstractFlickrEntity) entity).setDatabaseSession(database);
			try {
				entity.load();
			} catch (FlickrEntityException ex) {
				System.out.println("FlickrEntityException: " + ex.getMessage());
				System.out.println(ex.getCause().getMessage());
			}
			System.out.println("DONE: top-photos_" + stringDate);
			date.setTime(date.getTime() + 1000 * 3600 * 24);
		}
	}
}
