package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test of Downloader class
 * 
 * @author Jan Drabek
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
