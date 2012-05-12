/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.ApiException;
import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.BadUrlException;
import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.ServiceCurrentlyUnavilableException;
import cz.muni.fi.pb138.flickrgraphr.api.Exceptions.UserNotFoundException;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author mantaexx
 */
public class FlickAPI {

    private static final String BASE_URL = "http://api.flickr.com/services/rest/?";
    private static XPath xpath = null;

    private static XPath getXpathInstance() {
        if (xpath == null) {
            xpath = XPathFactory.newInstance().newXPath();
        }
        return xpath;
    }

    public static String getIdFromName(String name) {
        String url = BASE_URL + "method=flickr.people.findByUsername&"
                + "api_key=" + Downloader.API_KEY + "&username=" + name
                + "&format=rest";

        return getID(url);
    }

    public static String getIdFromEmail(String email){
        String url = BASE_URL + "method=flickr.people.findByEmail&"
                + "api_key=" + Downloader.API_KEY + "&find_email=" + email
                + "&format=rest";

        return getID(url);
    }

    private static String getID(String requestUrl) {
        String xmlResponse = Downloader.download(requestUrl);

        Document result = XmlDocumentBuilder(xmlResponse);

        if (result == null) {
            throw new DownloaderException("Response is not valid xml document");
        }
        String userID = null;
        try {
            XPathExpression userIdExpr = getXpathInstance().compile("/rsp/user/@nsid");
            userID = (String) userIdExpr.evaluate(result, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            throw new ApiException("Error compiling xpath expression.");
        }

        return userID;
    }

    /**
     * This method converts string with xml document into Document
     *
     * @param xmlString string with xml data
     * @return XML Document if success, otherwise null
     */
    private static Document XmlDocumentBuilder(String xmlString) {
        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = dbf.newDocumentBuilder();

            ByteArrayInputStream xmlBytes = new ByteArrayInputStream(xmlString.getBytes());

            doc = builder.parse(xmlBytes);
            try {

                XPathExpression failCountExpr = getXpathInstance().compile("count(/rsp[@stat=\"fail\"])");
                XPathExpression errCodeExpr = getXpathInstance().compile("/rsp/err/@code");

                Double fc = (Double) failCountExpr.evaluate(doc, XPathConstants.NUMBER);

                if (fc > 0) {
                    int errCode = ((Double) errCodeExpr.evaluate(doc, XPathConstants.NUMBER)).intValue();

                    if (errCode == 1) {
                        throw new UserNotFoundException();
                    }
                    if (errCode == 105) {
                        throw new ServiceCurrentlyUnavilableException();
                    }
                    if (errCode == 116) {
                        throw new BadUrlException();
                    }

                    throw new ApiException("Flickr error code: " + Integer.toString(errCode));
                }
            } catch (XPathExpressionException ex) {
                throw new ApiException("Problem in xpath expression compilation.");
            }


        } catch (IOException ex) {
            throw new ApiException("Error in IO Operation");
        } catch (ParserConfigurationException ex) {
            throw new ApiException("Problem with parser");
        } catch (SAXException ex) {
            throw new ApiException("Problem with parser.");
        }
        return doc;
    }
}
