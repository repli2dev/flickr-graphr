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
public class TopPhotos extends AbstractFlickrEntity {
	
	// Constants
	/** @var URL for query with place-holders */
	private final String URL = "http://api.flickr.com/services/rest/"
                + "?method=flickr.interestingness.getList&api_key=<!--API_KEY-->&date=<!--DATE-->"
                + "&extras=views&per_page=500&format=rest";
        /** @var Name of database used for storing data */
        private final String DATABASE = "top-photos";
	
	// Inner data
	private String data;
	private String outputData;
        private String date;

        // FIXME Give 'date' as String or Date?
	/**
	 * Create new TopPhotos object with the date specified
	 * @param context 
	 * @param date date to process
	 */
	public TopPhotos(ServletContext context, String date) {
		this.context = context;
		this.date = date;
	}
        
	/**
	 * Create new TopPhotos object and sets yesterday as date
	 * @param context 
	 */
	public TopPhotos(ServletContext context) {
		this.context = context;
		this.date = getYesterdayDate();
	}

	@Override
	public void load() throws FlickrEntityException {
		getData();
                validateXML(data,"/xml/scheme/flickr_api_interestingness.xsd",
                            "flickr.interestingness");
		transform();
                //just double-checking, to preserve db consistency
                validateXML(outputData,"/xml/scheme/graphr_db_top_photos.xsd", 
                            "graphr.top-photos");
                saveToDababase(DATABASE, date, getOutputAsInputStream());
	}

        // FIXME Why enabling writeback here?
	@Override
	public void unload() throws FlickrEntityException {
                deleteFromDatabase(DATABASE, getOldDate());
	}
	
	private InputStream getOutputAsInputStream() {
		byte[] barray = outputData.getBytes();
		return new ByteArrayInputStream(barray); 
	}
	
	private String getUrl() {
		// Prepare URL and fetch result
		String finalURL = URL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
		finalURL = finalURL.replaceAll("<!--DATE-->", date);
		return finalURL;
	}
	
	private void getData() throws FlickrEntityException {
		try {
			data = Downloader.download(getUrl());
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
			transformer.setParameter("DATE", date);
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new FlickrEntityException("XSLT transformation of downloaded 'hot list' failed (check the input).",ex);
		}
		this.outputData = outputData.toString();
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
	
        /*
	private ClientSession getDatabase() {
		BaseXSession bxs = getDatabaseSession();
		return bxs.get(DATABASE, true);
	}*/

}
