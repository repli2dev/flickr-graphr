package cz.muni.fi.pb138.flickrgraphr.tools;

import java.io.*;
import java.util.Scanner;

/**
 * Often used function with I/O (e.g. reading file etc.)
 *
 * @author Jan Drábek
 */
public class IOHelper {

	public static String fileToString(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		return new Scanner(in).useDelimiter("\\A").next();
	}

	public static String fileToString(File file) throws FileNotFoundException {
		return new Scanner(file).useDelimiter("\\A").next();
	}
}
