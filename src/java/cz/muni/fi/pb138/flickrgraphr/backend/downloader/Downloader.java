/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This is simple class with static method for downloading web content
 *
 * @author mantaexx
 */
public class Downloader {

    /**
     * This is method for download requested web page
     *
     * @param requestedUrl string with requested url. Exception is thrown if
     * invalid
     * @return returns string with page content if success
     * @throws DownloaderException this exception is thrown if sth goes wrong.
     * Contains message with description and forwarded Cause
     */
    public static String Download(String requestedUrl) throws DownloaderException {
        
        if(requestedUrl == null) throw new DownloaderException("Null pointer - requestedUrl");

        URL url = null;
        URLConnection conn = null;
        BufferedReader bfr = null;
        InputStreamReader isr = null;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL(requestedUrl);
            conn = url.openConnection();

            isr = new InputStreamReader(conn.getInputStream());
            bfr = new BufferedReader(isr);

            String inputLine;

            while ((inputLine = bfr.readLine()) != null) {
                result.append(inputLine);
            }

            return result.toString();


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
