package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

import cz.muni.fi.pb138.flickrgraphr.tools.IOHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This is simple class with static method for downloading web content
 *
 * @author Josef Ludvicek
 */
public class Downloader {

	public static final String API_KEY = "ed6204a240a139921dbbc30c1a1e6f65";

	/**
	 * This is method for download requested web page
	 *
	 * @param requestedUrl string with requested url. Exception is thrown if
	 * invalid
	 * @return returns string with page content if success
	 * @throws DownloaderException this exception is thrown if sth goes
	 * wrong. Contains message with description and forwarded Cause
	 */
	public static String download(String requestedUrl) throws DownloaderException {

		if (requestedUrl == null) {
			throw new DownloaderException("Null pointer - requestedUrl");
		}

		URL url = null;
		URLConnection conn = null;
		BufferedReader bfr = null;

		try {
			url = new URL(requestedUrl);
			conn = url.openConnection();

			return IOHelper.fileToString(conn.getInputStream());

		} catch (MalformedURLException ex) {
			throw new DownloaderException("malformed url", ex.getCause());
		} catch (IOException ex) {
			throw new DownloaderException("connection error", ex.getCause());
		} finally {
			try {
				if (bfr != null) {
					bfr.close();
				}
			} catch (IOException ex) {
				throw new DownloaderException(
					"connection error while closing connection",
					ex.getCause());
			}

		}
	}
}
