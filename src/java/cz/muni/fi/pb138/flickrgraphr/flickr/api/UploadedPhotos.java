package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Represents one processing of Flickr API - search->count of uploaded photos It
 * takes care of downloading, validation and transforming the received data and
 * its (de)storing into database Produces 'count of uploaded photos' of the day
 *
 * @author Martin Ukrop, Jan Drabek
 */
public class UploadedPhotos extends AbstractFlickrEntity {

	// Constants
	/**
	 * @var URL for query with place-holders
	 */
	private final String URL = "http://api.flickr.com/services/rest/"
		+ "?method=flickr.photos.search&api_key=<!--API_KEY-->&min_upload_date=<!--FROM_DATE-->&max_upload_date=<!--TO_DATE-->"
		+ "&per_page=500&format=rest";
	/**
	 * @var Name of database used for storing data
	 */
	private final String DATABASE = "uploaded-photos";
	// Inner data
	private String data;
	private String outputData;
	private Date date;

	/**
	 * Create new Users object with the date specified
	 *
	 * @param context
	 * @param date date to process
	 */
	public UploadedPhotos(ServletContext context, Date date) {
		this.context = context;
		this.date = date;
	}

	/**
	 * Create new Users object and sets yesterday as date
	 *
	 * @param context
	 */
	public UploadedPhotos(ServletContext context) {
		this.context = context;
		this.date = DateTimeHelper.yesterday();
	}

	@Override
	public void load() throws FlickrEntityException {
		getData();
		// FIXME: Title gets "&" in it sometimes...
		data = data.replace("&", "&amp;");
		validateXML(data, "/xml/scheme/flickr_api_uploaded_photos.xsd",
			"flickr.photos.search", true);
		transform();

		//just double-checking, to preserve db consistency
		validateXML(outputData, "/xml/scheme/graphr_db_uploaded_photos.xsd",
			"graphr.uploaded_photos", true);
		saveToDababase(DATABASE, DateTimeHelper.formatDate(date), getAsInputStream(outputData));
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
		finalURL = finalURL.replaceAll("<!--FROM_DATE-->", DateTimeHelper.formatDate(date) + "00:00:00");
		finalURL = finalURL.replaceAll("<!--TO_DATE-->", DateTimeHelper.formatDate(date) + "23:59:59");
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
			throw new FlickrEntityException("Downloading of 'Count of uploaded photos' failed.", ex);
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
		// TODO generalise XSLT transformation (ideally to AbstractFlickrEntity)
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Source source = new StreamSource(new StringReader(data));
		Source xslt = null;
		try {
			xslt = new StreamSource(getPath("/xml/xslt/flickr_uploaded_photos.xslt").openStream());
		} catch (MalformedURLException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'count of uploaded photos' failed.", ex);
		} catch (IOException ex) {
			throw new FlickrEntityException("XSLT tranformation of downloaded  'count of uploaded photos' failed.", ex);
		}
		StringWriter outputData = new StringWriter();
		Result result = new StreamResult(outputData);
		Transformer transformer;
		try {
			transformer = tfactory.newTransformer(xslt);
			transformer.setParameter("DATE", DateTimeHelper.formatDate(date));
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			throw new FlickrEntityException("XSLT transformation of downloaded 'count of uploaded photos' failed (check the input).", ex);
		}
		this.outputData = outputData.toString();
	}
}
