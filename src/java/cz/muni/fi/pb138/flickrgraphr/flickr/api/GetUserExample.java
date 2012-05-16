package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;

/**
 *
 * @author Martin Ukrop
 */
public class GetUserExample {
    
    	// FIXME setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";	
    
	public static void main(String [] args){
                //FlickrEntity entity = new GetUser(null,"Brad.Wagnner",false);
                FlickrEntity entity = new GetUser(null,"dandelion8888@gmail.com",true);
                ((GetUser) entity).setPath(ROOT_PATH);
                try {
                    //User user = ((GetUser) entity).fromName();
                    User user = ((GetUser) entity).fromEmail();
                    System.out.println(user.getId());
                    System.out.println(user.getDisplayName());
                } catch (FlickrEntityException ex) {
                    System.out.println("FlickrEntityException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
        }
}