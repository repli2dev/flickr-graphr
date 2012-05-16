package cz.muni.fi.pb138.flickrgraphr.flickr.api;

/**
 *
 * @author martin
 */
public class User {
    
    private String id;
    private String displayName;
    
        public User(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
        
        public Boolean isValid() {
            if (id != null && displayName != null) {
                return true;
            }
            return false;
        }
        
        public String getId() {
            return id;
        }
        
        public String getDisplayName() {
            return displayName;
        }
}
