package cz.muni.fi.pb138.flickrgraphr.flickr.api;

/**
 * Exception to be thrown when something in FlickrEntity goes wrong
 * @author Jan Drabek
 */
public class FlickrEntityException extends Exception {

	public FlickrEntityException() {
		super();
	}

	public FlickrEntityException(String message) {
		super(message);
	}

	public FlickrEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlickrEntityException(Throwable cause) {
		super(cause);
	}
	
}
