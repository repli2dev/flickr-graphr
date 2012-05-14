package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jan Drabek
 */
public class ImportWeekTopTags {
	
	private static final String DOCUMENT_PATH = "file:///home/jan/TEMP/hotListsToImport/";	
    
	private static final String ROOT_PATH = "file:///home/jan/TEMP/flickr-graphr/src/";	
    
	/**
	 * Iterate through given dir and import import all .xml files
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String [] args) throws MalformedURLException, FileNotFoundException{
		BaseXSession database = new BaseXSession("localhost", 1984, "admin", "admin");
		
		File dir = new File(new URL(DOCUMENT_PATH).getPath());
		String[] children = dir.list(new XMLFilter());
		String content;
		int count = 0;
		if(children != null) {
			for(String name : children) {
				content = new Scanner(new File(new URL(DOCUMENT_PATH + name).getPath())).useDelimiter("\\A").next();
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
	
	private static Date parseDateFromFilename(String name) throws ParseException {
		String temp = name;
		temp = temp.replace("hotlist-", "");
		temp = temp.replace(".xml", "");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(temp);
	}
}
class XMLFilter implements FilenameFilter {
	@Override
	public boolean accept(File file, String name) {
		return name.endsWith(".xml");
	}
	
}
