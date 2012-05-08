/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.backend.downloader;

/**
 * Small sample of how to use downloader...
 * @author mantaexx
 */
public class DownloaderSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("starting download");
        try {
            String response = Downloader.Download("http://is.muni.cz");
            System.out.println(response);
        } catch (DownloaderException ex) {
            System.err.println("hoops, something bad happend:\n" + ex.getMessage());
            System.err.println("cause: \n" + ex.getCause());
        }
    }
}
