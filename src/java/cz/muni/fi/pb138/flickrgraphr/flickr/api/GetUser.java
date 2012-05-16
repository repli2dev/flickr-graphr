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

/**
 * Represents one processing of Flickr API - interestingness->top-photos
 * It takes care of downloading, validation and transforming the received data and its (de)storing into database
 * Produces 'top-photos' of the day
 * @author Martin Ukrop
 */
public class GetUser extends AbstractFlickrEntity {
	
	// Constants
	/** @var URL for query with place-holders */
	private final String URL_NAME2ID = "http://api.flickr.com/services/rest/"
                + "?method=flickr.people.findByUsername&api_key=<!--API_KEY-->&username=<!--NAME-->"
                + "&format=rest";
        /** @var URL for query with place-holders */
	private final String URL_EMAIL2ID = "http://api.flickr.com/services/rest/"
                + "?method=flickr.people.findByEmail&api_key=<!--API_KEY-->&find_email=<!--EMAIL-->"
                + "&format=rest";
       /** @var URL for query with place-holders */
	private final String URL_ID2NAME = "http://api.flickr.com/services/rest/"
                + "?method=flickr.people.getInfo&api_key=<!--API_KEY-->&user_id=<!--ID-->"
                + "&format=rest";
	
	// Inner data
	private String data;
	private String outputData;
        private String id;
        private String displayName;
        private String email;

	/**
	 * // todo
	 * @param context 
	 * @param date date to process
	 */
	public GetUser(ServletContext context, String value, Boolean isEmail) {
		this.context = context;
                if (isEmail) {
                        this.email = value;
                } else {
                        this.displayName = value;
                }
	}

        public User fromEmail(String email) {
            return new User(null,null);
        }
        
        public User fromName(String email) {
            //getData(1);
            
            return new User(null,null);
        }
        
	@Override
	public void load() throws FlickrEntityException {
		//does nothing
                //getData();
                validateXML(data,"/xml/scheme/flickr_api_interestingness.xsd",
                            "flickr.interestingness");
		transform();
                //just double-checking, to preserve db consistency
                validateXML(outputData,"/xml/scheme/graphr_db_top_photos.xsd", 
                            "graphr.top-photos");
                //saveToDababase(DATABASE, date, getOutputAsInputStream());
	}

	@Override
	public void unload() throws FlickrEntityException {
                // does nothing
	}
	
	private InputStream getOutputAsInputStream() {
		byte[] barray = outputData.getBytes();
		return new ByteArrayInputStream(barray); 
	}
	
	private String getUrl(int method) {
		// Prepare URL and fetch result
                String finalURL;
                switch (method) {
                    case 1 :
                        finalURL = URL_NAME2ID;
                        break;
                    case 2 :
                        finalURL = URL_EMAIL2ID;
                        break;
                    default :
                        finalURL = URL_ID2NAME;
                        break;
                }
                finalURL = finalURL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
		finalURL = finalURL.replaceAll("<!--NAME-->", displayName);
                finalURL = finalURL.replaceAll("<!--EMAIL-->", email);
                finalURL = finalURL.replaceAll("<!--ID-->", id);
		return finalURL;
	}
        
	private void getData(int method) throws FlickrEntityException {
		try {
			data = Downloader.download(getUrl(method));
		} catch (DownloaderException ex) {
			throw new FlickrEntityException("Downloading of 'interestingness' failed.", ex);
		}
	}
	
	private void transform() throws FlickrEntityException {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Source source = new StreamSource(new StringReader(data));
                Source xslt = null;
		try {
			xslt = new StreamSource(getPath("/xml/xslt/flickr_interestingness_to_top_photos.xslt").openStream());
		} catch (MalformedURLException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'interestingness' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'interestingness' failed.", ex);
		}
		StringWriter outputData = new StringWriter();
		Result result = new StreamResult(outputData);
		Transformer transformer;
		try {
			transformer = tfactory.newTransformer(xslt);
			transformer.setParameter("DATE", id);
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new FlickrEntityException("XSLT transformation of downloaded 'hot list' failed (check the input).",ex);
		}
		this.outputData = outputData.toString();
	}
	
	/*private String getYesterdayDate() {
                Date date = now();
                date.setTime(date.getTime()-24*3600*1000);
		return formatDate(date);
	}
	
	private String getOldDate() {
		Date date = now();
		date.setTime(date.getTime()-14*24*3600*1000);
		return formatDate(date);
	}*/
	
        /*
	private ClientSession getDatabase() {
		BaseXSession bxs = getDatabaseSession();
		return bxs.get(DATABASE, true);
	}*/

}
