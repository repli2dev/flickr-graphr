package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Recomputes top-users data for a given date range and saves it to DB
 * @author Martin Ukrop
 */
public class ComputeTopUsers {
    
        private static final String BEGIN_DATE = "2012-05-18";
        private static final String END_DATE = "2012-05-18";
        
    	// FIXME setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";	
    
	public static void main(String [] args) throws ParseException {
                BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(BEGIN_DATE);
                Date endDate = dateFormat.parse(END_DATE);
		
                while (!date.after(endDate)) {
                    String stringDate = dateFormat.format(date);
                    FlickrEntity entity = new TopUsers(null,stringDate);
                    ((AbstractFlickrEntity) entity).setPath(ROOT_PATH);
                    ((AbstractFlickrEntity) entity).setDatabaseSession(database);
                    try {
			entity.load();
                    } catch (FlickrEntityException ex) {
			System.out.println("FlickrEntityException: " + ex.getMessage());
			System.out.println(ex.getCause().getMessage());
                    }
                    System.out.println("DONE: top-photos_"+stringDate);
                    date.setTime(date.getTime()+1000*3600*24);
                }
	}
}
