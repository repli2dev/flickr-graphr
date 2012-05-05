package cz.muni.fi.pb138.flickrgraphr.dbtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;
import org.apache.commons.io.FileUtils;

/**
 * Test class working with BaseX database
 * @author Jan Drabek, Martin Ukrop
 * 
 * To run this file, following conditions must be met:
 *      - BaseX server must be running
 *      - rootPath must be correctly set (line ~30)
 */

public class DbTest {
	public static void main(String [] args) throws IOException {
                
                // Set your own path for flickr-graphr directory
                String rootPath = "/home/martin/Documents/flickr-graphr/";
                //String rootPath = "/home/jan/TEMP/flickr-graphr/";
                
                // Set other paths
                String dbPath = rootPath+"src/db/";
                String docPath = rootPath+"src/xml-data/temp2/";
                String xqPath = rootPath+"src/xml-data/xq2/";
                
                // Clean db folders
                FileUtils.deleteDirectory(new File(dbPath+"best_people"));
                FileUtils.deleteDirectory(new File(dbPath+"best_photos"));
                
                // Create connection
		ClientSession session = null;
		try {
			session = new ClientSession("localhost", 1984, "admin", "admin");
		} catch (IOException ex) {
			System.out.println("Server connetion failed.");
			Logger.getLogger(DbTest.class.getName()).log(Level.SEVERE, null, ex);
                        throw new IOException("Not connected to server. Exiting.");
		}
		System.out.println("Server connection established.");
                
		// Check if database exists. If yes, drop it to avoid collisions.
                // Then create fresh db named graphr
		if(session.execute("XQUERY db:exists('graphr')").equals("true")) {
                        System.out.println("Database 'graphr' existed. Now dropped.");
                        session.execute("DROP DB graphr");
                }
                session.execute("CREATE DB graphr " + dbPath);
                
		// Open database, set otions
		session.execute("OPEN graphr");
                if (session.execute("GET WRITEBACK").equals("WRITEBACK: false\n")) {
                        session.execute("SET WRITEBACK true");
                        System.out.println("WRITEBACK now set TRUE.");
                }
                
		// Add documents (temporary solution)
                for(int i=17; i<=30; i++) {
                       session.execute("ADD TO best_photos/best_photos_2012-04-"+i+".xml "+
                               docPath+"best_photos_2012-04-"+i+".xml");
                }
                
                // Count data for people for 2012-04-30
                // Create new db file -- bad solution, but works for now
                session.execute("ADD TO best_people/best_people_2012-04-30.xml "+docPath+"empty_db_file.xml");
                // Read query from file
                String query = FileUtils.readFileToString(new File(xqPath+"compute_people_for_date.xq"), "UTF-8");
                ClientQuery cq = session.query(query);
                // Bind needed variables, run query
                cq.bind("date_processed", "2012-04-30");
                cq.bind("max_delay", "7");
                cq.bind("output_file", "graphr/best_people/best_people_2012-04-30.xml");
                System.out.println(cq.execute());
                
		// Make simple query and print result
		System.out.println(session.execute("XQUERY count(collection('graphr')//photo)"));
                
		// Drop database, close connection
                session.execute("DROP DB graphr");
		session.close();
	}
}
