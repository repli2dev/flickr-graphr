package cz.muni.fi.pb138.flickrgraphr.tools;

import java.io.*;
import java.util.Scanner;

/**
 * Often used function with I/O (e.g. reading file etc.)
 *
 * @author Jan Drábek
 */
public class IOHelper {

        /**
         * Returns contents of the given file (InputStream) as a String
         * @param is
         * @return file contents
         */
	public static String fileToString(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		return new Scanner(in).useDelimiter("\\A").next();
	}

        /**
         * Returns contents of the given file as a String
         * @param file
         * @return file contents
         * @throws FileNotFoundException 
         */
	public static String fileToString(File file) throws FileNotFoundException {
		return new Scanner(file).useDelimiter("\\A").next();
	}
}
