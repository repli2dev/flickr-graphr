package cz.muni.fi.pb138.flickrgraphr.flickr.api.batch;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.AbstractFlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.Users;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO unify import classes for older data (now is mostly copy-paste)

/**
 * Downloads user information from Flickr for a given date range and saves it to DB
 * @author Martin Ukrop
 */
public class ImportUsers {
    
        private static final String BEGIN_DATE = "2012-05-18";
        private static final String END_DATE = "2012-05-18";
        
    	// FIXME setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";
        
	public static void main(String [] args) throws ParseException {
                BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");
                
                Date date = DateTimeHelper.parseDate(BEGIN_DATE);
                Date endDate = DateTimeHelper.parseDate(END_DATE);
		
                while (!date.after(endDate)) {
                    String stringDate = DateTimeHelper.formatDate(date);
                    FlickrEntity entity = new Users(null,stringDate);
                    ((AbstractFlickrEntity) entity).setPath(ROOT_PATH);
                    ((AbstractFlickrEntity) entity).setDatabaseSession(database);
                    try {
			entity.load();
                    } catch (FlickrEntityException ex) {
			System.out.println("FlickrEntityException: " + ex.getMessage());
			System.out.println(ex.getCause().getMessage());
                    }
                    System.out.println("DONE: users_"+stringDate);
                    date.setTime(date.getTime()+1000*3600*24);
                }
	}
}
