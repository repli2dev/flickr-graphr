package cz.muni.fi.pb138.flickrgraphr.flickr.api;

//TODO delete entire class

/**
 * Example how to use GetUser FlickrEntity
 *
 * @author Martin Ukrop
 */
public class GetUserExample {

	// setting for root path to project sources
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";

	public static void main(String[] args) {
		//FlickrEntity entity = new GetUser(null,"Chris(C) & Sue (S) M-T",false);
		FlickrEntity entity = new GetUser(null, "email@gmail.com", true);
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
