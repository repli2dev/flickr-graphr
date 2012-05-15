package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.DatabaseQueryException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;

/**
 * Example usage of DatabaseQuery
 * @author Martin Ukrop
 */
public class QueryExample {
    
        /*
         * 2 veci, co chcem prebrat zajtra:
         * - nevytvorit balik cz.muni.fi.pb138.flickrgraphr.api.dbquery ? (prehladnost kodu)
         * - vytvorit podtriedy QueryException, ktore budu rozlisovat, co sa stalo?
         *   (akoze ParameterNotSetException, DataNotInDatabaseException, CouldNotConnectToDatabaseException, ...)
         */

    	// FIXME temporary solution, set your own home when running
	private static final String ROOT_PATH = "file:///home/martin/Documents/flickr-graphr/src";	
    
	public static void main(String [] args){
                BaseXSession dbSession = new BaseXSession("localhost", 1984, "admin", "admin");
                String result;
                
                // query for top-users
                DatabaseQuery query1 = new QueryTopUsers(null);
                query1.setDatabaseSession(dbSession);
                // temporary solution, to be removed
                ((AbstractDatabaseQuery) query1).setPath(ROOT_PATH);
                try {
                    query1.setParameter("beginDate", "2012-04-20");
                    query1.setParameter("endDate", "2012-04-30");
                    query1.setParameter("userId", "38177739@N05");
                    result = query1.execute();
                    System.out.println(result);
                } catch (DatabaseQueryException ex) {
                    System.out.println("DatabaseQueryException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
                
                // query for top-tags
                DatabaseQuery query2 = new QueryTopTags(null);
                query2.setDatabaseSession(dbSession);
                // temporary solution, to be removed
                ((AbstractDatabaseQuery) query2).setPath(ROOT_PATH);
                try {
                    query2.setParameter("tag", "best-photo-tag");
                    query2.setParameter("type", "score");
                    result = query2.execute();
                    System.out.println(result);
                } catch (DatabaseQueryException ex) {
                    System.out.println("DatabaseQueryException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
                
                // query to save new display name to database
                DatabaseQuery query3 = new QueryAddUser(null);
                query1.setDatabaseSession(dbSession);
                // temporary solution, to be removed
                ((AbstractDatabaseQuery) query3).setPath(ROOT_PATH);
                try {
                    query3.setParameter("userID", "38177739@N05");
                    query3.setParameter("displayName", "My Wonderful Name");
                    result = query3.execute();
                    System.out.println(result);
                } catch (DatabaseQueryException ex) {
                    System.out.println("DatabaseQueryException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
                
                // query to get top ids for given day
                DatabaseQuery query4 = new QueryTopIdsForDay(null);
                query4.setDatabaseSession(dbSession);
                // temporary solution, to be removed
                ((AbstractDatabaseQuery) query3).setPath(ROOT_PATH);
                try {
                    query4.setParameter("date", "2012-04-30");
                    query4.setParameter("count", "10");
                    result = query4.execute();
                    System.out.println(result);
                } catch (DatabaseQueryException ex) {
                    System.out.println("DatabaseQueryException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
                
                // query to retrieve user-id based on display-name
                // zatial nemusis pouzivat, ale myslim si, ze bude strasne pomale pytat sa Flickru
                DatabaseQuery query5 = new QueryGetUserId(null);
                query5.setDatabaseSession(dbSession);
                // temporary solution, to be removed
                ((AbstractDatabaseQuery) query5).setPath(ROOT_PATH);
                try {
                    query5.setParameter("displayName", "My Wonderful Name");
                    result = query5.execute();
                    System.out.println(result);
                } catch (DatabaseQueryException ex) {
                    System.out.println("DatabaseQueryException: " + ex.getMessage());
                    System.out.println(ex.getCause().getMessage());
                }
        }
}
