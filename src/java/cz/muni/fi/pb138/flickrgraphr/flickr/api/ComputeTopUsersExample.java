package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import javax.sound.midi.SysexMessage;

/**
 *
 * @author Martin Ukrop
 */
public class ComputeTopUsersExample {
    
    	// FIXME setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";	
    
	public static void main(String [] args){
		BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");
		for (int i=1; i<=30; i++) {
                    String date = "2012-04-" + String.format("%02d", i);
                    FlickrEntity entity = new TopUsers(null,date);
                    ((TopUsers) entity).setPath(ROOT_PATH);
                    ((TopUsers) entity).setDatabaseSession(database);
                    try {
			entity.load();
                    } catch (FlickrEntityException ex) {
			System.out.println("FlickrEntityException: " + ex.getMessage());
			System.out.println(ex.getCause().getMessage());
                    }
                    System.out.println("DONE: "+date);
                }
        }
}
