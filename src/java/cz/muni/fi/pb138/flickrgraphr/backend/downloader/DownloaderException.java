package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

/**
 * Exception thrown by Downloader class. It extends RuntimeException
 *
 * @author mantaexx
 */
public class DownloaderException extends RuntimeException {

	public DownloaderException(Throwable cause) {
		super(cause);
	}

	/**
	 * This is suggested way of creating this exception
	 *
	 * @param message Message describing what happend
	 * @param cause cause from original exception eg IOException.getCause()
	 */
	public DownloaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public DownloaderException(String message) {
		super(message);
	}

	public DownloaderException() {
	}
}
