package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;

/**
 * //TODO documentation
 * @author Martin Ukrop
 */
public interface DatabaseQuery {
        
        String execute() throws DatabaseQueryException;
        
        void setParameter(String name, String value) throws DatabaseQueryException;
        
        void setDatabaseSession(BaseXSession database);
        
}
