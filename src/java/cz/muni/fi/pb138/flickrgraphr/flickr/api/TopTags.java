package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Date;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Represents one page of Flickr API - Hot list->hot-list
 * It takes care of downloading, validation and transforming the received data and its (de)storing into database
 * @author Jan Drabek
 */
public class TopTags extends AbstractFlickrEntity {
	
	// Constants
	/** @var URL for query with placeholders */
	private final String URL = "http://api.flickr.com/services/rest/"
                + "?method=flickr.tags.getHotList&api_key=<!--API_KEY-->"
                + "&period=<!--TYPE-->&count=200&format=rest";
	/** @var Name of database used for storing data */
	private final String DATABASE = "tags";	
	
	// Inner data
	private String data;
	private String outputData;
        private String type;
	private Date date;

	/**
	 * Create new hot list object
	 * @param context 
	 * @param type week | day
	 */
	public TopTags(ServletContext context, String type) {
		this.context = context;
		this.type = type;
	}
	
        /**
         * Loads data of given type (for importing older and external data)
         * @param type
         * @param data 
         */
	public TopTags(String type, String data) {
		this.type = type;
		this.data = data;
	}
	

	@Override
	public void load() throws FlickrEntityException {
		getData();
                validateXML(data,"/xml/scheme/flickr_api_hot_list.xsd","flickr.hotList",true);
                transform();
                //just double-checking, to preserve db consistency
                validateXML(outputData,"/xml/scheme/graphr_db_tags.xsd","graphr.hot-list",true);
                saveToDababase(DATABASE+ "-" + type, DateTimeHelper.formatDate(date), getAsInputStream(outputData));
	}

	@Override
	public void unload() throws FlickrEntityException {
		// Remove data older than 14 days
                deleteFromDatabase(DATABASE+ "-" + type, DateTimeHelper.formatDate(DateTimeHelper.shiftDate(14*24*3600*1000)));
	}
	
        /**
         * returns URL for specific API request (replaces place-holders of parameters)
         * @return 
         */
	private String getUrl() {
		// Prepare URL and fetch result
		String finalURL = URL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
		finalURL = finalURL.replaceAll("<!--TYPE-->", type);
		return finalURL;
	}
	
        /**
         * Downloads the API response from Flickr
         * @throws FlickrEntityException 
         */
	private void getData() throws FlickrEntityException {
		// Skip fetching data if they are already there
		if(data != null) return;
		try {
			data = Downloader.download(getUrl());
		} catch (DownloaderException ex) {
			throw new FlickrEntityException("Downloading of 'hot list' failed.", ex);
		}
	}
	
        /**
         * runs the needed XSLT transformation on data saved in attribute 'data'
         * bind XSLT parameters according to class attribute values
         * puts resulting string to 'outputData'
         * @throws FlickrEntityException 
         */
	private void transform() throws FlickrEntityException {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Source source = new StreamSource(new StringReader(data));
                Source xslt = null;
		try {
			xslt = new StreamSource(getPath("/xml/xslt/flickr_hotlist_to_tags_by_week.xslt").openStream());
		} catch (MalformedURLException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'hot list' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'hot list' failed.", ex);
		}
		StringWriter outputData = new StringWriter();
		Result result = new StreamResult(outputData);
		Transformer transformer;
		try {
			transformer = tfactory.newTransformer(xslt);
			transformer.setParameter("DATE", DateTimeHelper.formatDate(date));
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new FlickrEntityException("XSLT transformation of downloaded 'hot list' failed (check the input).",ex);
		}
		this.outputData = outputData.toString();
	}
	
	public Date getDate() {
		if(date == null) {
			date = DateTimeHelper.now();
		}
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
}
