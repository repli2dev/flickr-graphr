package cz.muni.fi.pb138.flickrgraphr.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper for date and time
 * @author Jan Drábek
 */
public class DateTimeHelper {
	/**
	 * Return current date
	 * @return Current date
	 */
	public static Date now() {
		return Calendar.getInstance().getTime();
	}
	
	/**
	 * Return Date with subtracted num of seconds (use - to shift INTO THE FUTURE)
	 * @param seconds
	 * @return Shifted date
	 */
	public static Date shiftDate(long seconds) {
		Date date = now();
                date.setTime(date.getTime()-seconds);
		return date;
	}
	
	/**
	 * Return yesterday date
	 * @return  Yesterday date
	 */
	public static Date yesterday() {
		return shiftDate(24*3600*1000);
	}
	/**
	 * Parse date from formatted string (yyyy-MM-dd)
	 * @param input
	 * @return Date
	 */
	public static Date parseDate(String input) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.parse(input);
	}
	
	/**
	 * Return date in YYYY-MM-DD format
	 * @param date
	 * @return  Date formatted as string
	 */
	public static String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
        /**
	 * Return date in YYYY-MM-DD HH:mm:ss format
	 * @param date
	 * @return  Date formatted as string
	 */
        public static String formatDateTime(Date date) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                return dateFormat.format(date);
        }
}
