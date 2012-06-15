package cz.muni.fi.pb138.flickrgraphr.flickr.api.batch;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.TopTags;
import cz.muni.fi.pb138.flickrgraphr.tools.DateTimeHelper;
import cz.muni.fi.pb138.flickrgraphr.tools.IOHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

/**
 * Import data for week top tags (from local data as Flickr past data are not
 * available)
 *
 * @author Jan Drabek
 */
public class ImportWeekTopTags {

        // setting for root path to project sources
	private static final String DOCUMENT_PATH = "file:///home/jan/TEMP/hotListsToImport/";
	private static final String ROOT_PATH = "file:///home/jan/TEMP/flickr-graphr/src/";

	/**
	 * Iterate through given dir and import import all .xml files
	 *
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
		BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");

		File dir = new File(new URL(DOCUMENT_PATH).getPath());
		String[] children = dir.list(new XMLFilter());
		String content;
		int count = 0;
		if (children != null) {
			for (String name : children) {
				content = IOHelper.fileToString(new File(new URL(DOCUMENT_PATH + name).getPath()));
				FlickrEntity entity = new TopTags("week", content);
				((TopTags) entity).setPath(ROOT_PATH);
				((TopTags) entity).setDatabaseSession(database);
				try {
					((TopTags) entity).setDate(parseDateFromFilename(name));
				} catch (ParseException ex) {
					System.out.println("FAIL: " + name + " failed: " + ex.getMessage());
					continue;
				}
				try {
					entity.load();
				} catch (FlickrEntityException ex) {
					System.out.println("FAIL: " + name + " failed: " + ex.getMessage());
					continue;
				}
				count++;
				System.out.println("DONE: " + name);
			}
		}
		System.out.println("DONE: imported " + count + " documents.");
	}

        /**
         * Parses date from filemane of imported HotList files (format hotlist-YYYY-MM-DD.xml)
         * @param name
         * @return parsed date
         * @throws ParseException 
         */
	private static Date parseDateFromFilename(String name) throws ParseException {
		String temp = name;
		temp = temp.replace("hotlist-", "");
		temp = temp.replace(".xml", "");
		return DateTimeHelper.parseDate(temp);
	}
}

class XMLFilter implements FilenameFilter {

	@Override
	public boolean accept(File file, String name) {
		return name.endsWith(".xml");
	}
}
