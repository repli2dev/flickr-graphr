package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.basex.server.ClientSession;
import org.xml.sax.SAXException;
import java.net.URL;
import org.basex.server.ClientQuery;

/**
 * Represents processing of top-photos->top-users
 * It takes care of computing daily data and its (de)storing into database
 * Produces 'top-people' of the day
 * @author Martin Ukrop
 */
public class TopUsers extends AbstractFlickrEntity {
	
	// Constants
        /** @var Name of database used for storing data */
        private final String DATABASE = "top-users";
	
	// Inner data
	private String data;
	private String outputData;
        private String date;

	/**
	 * Create new TopUsers object with the date specified
	 * @param context 
	 * @param date date to process
	 */
	public TopUsers(ServletContext context, String date) {
		this.context = context;
		this.date = date;
	}
        
	/**
	 * Create new TopUsers object and sets yesterday as date
	 * @param context 
	 */
	public TopUsers(ServletContext context) {
		this.context = context;
		this.date = getYesterdayDate();
	}

	@Override
	public void load() throws FlickrEntityException {
		computeData();
                //just double-checking, to preserve db consistency
                validateXML(outputData,"/xml/scheme/graphr_db_top_users.xsd", 
                            "graphr.top-users");
                saveToDababase(DATABASE, date, getOutputAsInputStream());
	}

	@Override
	public void unload() throws FlickrEntityException {
		// Remove data older than 14 days
                deleteFromDatabase(DATABASE, getOldDate());
	}
	
	private InputStream getOutputAsInputStream() {
		byte[] barray = outputData.getBytes();
		return new ByteArrayInputStream(barray); 
	}
	
	private void computeData() throws FlickrEntityException {
                ClientSession session = getDatabase();
                try {
                        String queryString = readFileToString(getPath("/xml/xquery/topusers_from_topphotos.xq"));
                        ClientQuery cQuery = session.query(queryString);
                        cQuery.bind("date_processed", date);
                        cQuery.bind("max_delay", 7);
                        this.outputData = cQuery.execute();
                } catch (MalformedURLException ex) {
			throw new FlickrEntityException("XQuery transformation of 'top-photos' to 'top-users' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XQuery transformation of 'top-photos' to 'top-users' failed.", ex);
		}
	}
	
        private static String readFileToString(URL filePath) throws java.io.IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(filePath.openStream()));
                String line;
                String results = "";
                line = reader.readLine();
                while(line != null)
                {
                    results += line + "\n";
                    line = reader.readLine();
                }
                reader.close();
                return results;
        }
        
	private String getYesterdayDate() {
                Date date = now();
                date.setTime(date.getTime()-24*3600*1000);
		return formatDate(date);
	}
	
	private String getOldDate() {
		Date date = now();
		date.setTime(date.getTime()-14*24*3600*1000);
		return formatDate(date);
	}
	
        
	private ClientSession getDatabase() {
		BaseXSession bxs = getDatabaseSession();
		return bxs.get(DATABASE, true);
	}

}
