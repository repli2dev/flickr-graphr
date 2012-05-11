package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;

/**
 *
 * @author Jan Drabek
 */
public class ImportHotListExample {
	public static void main(String [] args){
		BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");
		FlickrEntity entity = new HotList(null,"week");
		((HotList) entity).setDatabaseSession(database);
		try {
			entity.load();
		} catch (FlickrEntityException ex) {
			System.out.println("FlickrEntityException: " + ex.getMessage());
			System.out.println(ex.getCause().getMessage());
		}
		System.out.println("DONE");
	}
}
