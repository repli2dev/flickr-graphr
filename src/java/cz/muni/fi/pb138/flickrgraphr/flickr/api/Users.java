package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Represents one processing of Flickr API - interestingness->users It takes
 * care of downloading, validation and transforming the received data and its
 * (de)storing into database Produces 'users' of the day
 *
 * @author Martin Ukrop
 */
public class Users extends AbstractFlickrEntity {

	// Constants
	/**
	 * @var URL for query with place-holders
	 */
	private final String URL = "http://api.flickr.com/services/rest/"
		+ "?method=flickr.interestingness.getList&api_key=<!--API_KEY-->&date=<!--DATE-->"
		+ "&extras=owner_name&per_page=500&format=rest";
	/**
	 * @var Name of database used for storing data
	 */
	private final String DATABASE = "users";
	// Inner data
	private String data;
	private String outputData;
	private String date;

	/**
	 * Create new Users object with the date specified
	 *
	 * @param context
	 * @param date date to process
	 */
	public Users(ServletContext context, String date) {
		this.context = context;
		this.date = date;
	}

	/**
	 * Create new Users object and sets yesterday as date
	 *
	 * @param context
	 */
	public Users(ServletContext context) {
		this.context = context;
		this.date = DateTimeHelper.formatDate(DateTimeHelper.yesterday());
	}

	@Override
	public void load() throws FlickrEntityException {
		getData();
		validateXML(data, "/xml/scheme/flickr_api_interestingness.xsd",
			"flickr.interestingness", true);
		transform();
		//just double-checking, to preserve db consistency
		validateXML(outputData, "/xml/scheme/graphr_db_users.xsd",
			"graphr.users", true);
		saveToDababase(DATABASE, date, getAsInputStream(outputData));
	}

	@Override
	public void unload() throws FlickrEntityException {
		// Remove data older than 14 days
		deleteFromDatabase(DATABASE, DateTimeHelper.formatDate(DateTimeHelper.shiftDate(14 * 24 * 3600 * 1000)));
	}

	/**
	 * returns URL for specific API request (replaces place-holders of
	 * parameters)
	 *
	 * @return
	 */
	private String getUrl() {
		// Prepare URL and fetch result
		String finalURL = URL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
		finalURL = finalURL.replaceAll("<!--DATE-->", date);
		return finalURL;
	}

	/**
	 * Downloads the API response from Flickr
	 *
	 * @throws FlickrEntityException
	 */
	private void getData() throws FlickrEntityException {
		try {
			data = Downloader.download(getUrl());
		} catch (DownloaderException ex) {
			throw new FlickrEntityException("Downloading of 'Interestingness' for 'users' failed.", ex);
		}
	}

	/**
	 * runs the needed XSLT transformation on data saved in attribute 'data'
	 * bind XSLT parameters according to class attribute values puts
	 * resulting string to 'outputData'
	 *
	 * @throws FlickrEntityException
	 */
	private void transform() throws FlickrEntityException {
		// generalise XSLT transformation could be generalised (ideally to .tools package)
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Source source = new StreamSource(new StringReader(data));
		Source xslt = null;
		try {
			xslt = new StreamSource(getPath("/xml/xslt/flickr_interestingness_to_users.xslt").openStream());
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
			throw new FlickrEntityException("XSLT transformation of downloaded 'interestingness' failed (check the input).", ex);
		}
		this.outputData = outputData.toString();
	}
}
