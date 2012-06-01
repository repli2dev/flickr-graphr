package cz.muni.fi.pb138.flickrgraphr.flickr.api;

/**
 * User entity - for tuple od id and displayName
 *
 * @author Martin Ukrop
 */
public class User {

	private String id;
	private String displayName;

	/**
	 * Create instance of user entity
	 * @param id
	 * @param displayName 
	 */
	public User(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	/**
	 * Check if entity is valid (both attributes are set)
	 * @return
	 */
	public Boolean isValid() {
		if (id != null && displayName != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns Id of user
	 * @params Id of user
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns DisplayName of user
	 * @return Display name
	 */
	public String getDisplayName() {
		return displayName;
	}
}
