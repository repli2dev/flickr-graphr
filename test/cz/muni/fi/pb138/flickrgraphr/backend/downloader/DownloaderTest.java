/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author jan
 */
public class DownloaderTest {

	public DownloaderTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of download method, of class Downloader.
	 */
	@Test
	public void testDownload() {
		try {
			Downloader.download(null);
			fail("No exception thrown.");
			Downloader.download("");
			fail("No MalformedURLException thrown.");
			Downloader.download("http:/atlas.cz");
			fail("No MalformedURLException thrown.");
		} catch (Exception ex) {
			// Silent
		}
	}
}
