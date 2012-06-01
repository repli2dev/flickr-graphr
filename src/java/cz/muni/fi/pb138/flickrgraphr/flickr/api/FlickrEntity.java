package cz.muni.fi.pb138.flickrgraphr.flickr.api;

/**
 * Abstraction over one Flickr API usage to provide operation and database
 * loading Servlet context and options (as date etc) are handled in constructor
 *
 * @author Jan Drabek
 */
public interface FlickrEntity {

	/**
	 * Load data from entity to the database
	 */
	void load() throws FlickrEntityException;

	/**
	 * Unload old data from database
	 */
	void unload() throws FlickrEntityException;
}
