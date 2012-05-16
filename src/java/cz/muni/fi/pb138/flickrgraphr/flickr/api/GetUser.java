package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import cz.muni.fi.pb138.flickrgraphr.backend.downloader.Downloader;
import cz.muni.fi.pb138.flickrgraphr.backend.downloader.DownloaderException;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Represents one processing of Flickr API - getting 'user-id' from either email or displayName
 * It takes care of downloading, validation and transforming the received data
 * Produces instance of 'User' with 'id' and 'displayName' set
 * @author Martin Ukrop, Josef Ludvicek
 */
public class GetUser extends AbstractFlickrEntity {
	
	// Constants
	/** @var URL for querying 'display-name' to get 'user-id' with place-holders */
	private final String URL_NAME2ID = "http://api.flickr.com/services/rest/"
                + "?method=flickr.people.findByUsername&api_key=<!--API_KEY-->&username=<!--NAME-->"
                + "&format=rest";
        /** @var URL for querying 'email' to get 'user-id' and 'display-name' with place-holders */
	private final String URL_EMAIL2ID = "http://api.flickr.com/services/rest/"
                + "?method=flickr.people.findByEmail&api_key=<!--API_KEY-->&find_email=<!--EMAIL-->"
                + "&format=rest";
	
	// Inner data
	private String data;
        private String id;
        private String displayName;
        private String email;

        /**
         * Create new GetUser object with the value of email XOR displayName specified
         * @param context
         * @param value     either email String or displayName String
         * @param isEmail   true, if 'value' is email | false if 'value' is displeyName
         */
	public GetUser(ServletContext context, String value, Boolean isEmail) {
		this.context = context;
                if (isEmail) {
                        this.email = value;
                } else {
                        this.displayName = value;
                }
	}
    
        /**
         * Determines 'user-id' and 'display-name' of a user based on his email
         * @return User entity with 'id' and 'displayName' set (if user exists)
         *                     or both 'id' and 'displeyName' as null (if user does not exist)
         * @throws FlickrEntityException 
         */
        public User fromEmail() throws FlickrEntityException {
            getData(true);
            try {
                validateXML(data,"/xml/scheme/flickr_api_usernameIdResult.xsd",
                            "flickr.findByEmail");
            } catch (FlickrEntityException ex) {
                try {
                    validateXML(data, "/xml/scheme/flickr_api_userNotFound.xsd",
                            "flickr.userNotFound");
                } catch (FlickrEntityException subEx) {
                    throw ex;
                }
                return new User(null,null);
            }
            this.id = getStringByXPath(data,"/rsp/user/@nsid");
            this.displayName = getStringByXPath(data,"/rsp/user/username/text()");
            return new User(id,displayName);
        }
        
        /**
         * Determines 'user-id' of a user based on his 'display-name'
         * @return User entity with 'id' and 'displayName' set (if user exists)
         *                     or both 'id' and 'displeyName' as null (if user does not exist)
         * @throws FlickrEntityException 
         */
        public User fromName() throws FlickrEntityException {
            getData(false);
            try {
                validateXML(data,"/xml/scheme/flickr_api_usernameIdResult.xsd",
                            "flickr.findByUsername");
            } catch (FlickrEntityException ex) {
                try {
                    validateXML(data, "/xml/scheme/flickr_api_userNotFound.xsd",
                            "flickr.userNotFound");
                } catch (FlickrEntityException subEx) {
                    throw ex;
                }
                return new User(null,null);
            }
            this.id = getStringByXPath(data,"/rsp/user/@id");
            return new User(id,displayName);
        }
        
        /**
         * extracts String from input data according to given XPath expression
         * @param input         input to process by XPath
         * @param xpathString   XPath expression to execute (return type is String Strictly!)
         * @return
         * @throws FlickrEntityException 
         */
        private String getStringByXPath(String input, String xpathString) throws FlickrEntityException {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                    DocumentBuilder builder = dbf.newDocumentBuilder();
                    Document doc = builder.parse(getAsInputStream(input));
                    XPathExpression xpathExpr = xpath.compile(xpathString);
                    return (String) xpathExpr.evaluate(doc,XPathConstants.STRING);
            } catch (XPathExpressionException ex) {
                throw new FlickrEntityException("XPath failed - incorrect expression.", ex);
            } catch (SAXException ex) {
                throw new FlickrEntityException("XPath failed - problem with evaluating", ex);
            } catch (ParserConfigurationException ex) {
                throw new FlickrEntityException("XPath failed - parser is not configured properly.", ex);
            } catch (IOException ex) {
                throw new FlickrEntityException("Xpath failed - IO problems.", ex);
            }
        }
        
	@Override
	public void load() throws FlickrEntityException {
		//does nothing
	}

	@Override
	public void unload() throws FlickrEntityException {
                // does nothing
	}
        
        /**
         * returns URL for specific API request (replaces place-holders of parameters)
         * @param isEmail  true, if 'value' is email | false if 'value' is displeyName
         * @return
         * @throws FlickrEntityException 
         */
	private String getUrl(boolean isEmail) throws FlickrEntityException {
                String finalURL;
                if (isEmail) {
                        finalURL = URL_EMAIL2ID;
                } else {
                        finalURL = URL_NAME2ID;
                }
                try {
                    finalURL = finalURL.replaceAll("<!--API_KEY-->", Downloader.API_KEY);
                    finalURL = finalURL.replaceAll("<!--NAME-->", displayName);
                    finalURL = finalURL.replaceAll("<!--EMAIL-->", email);
                } catch (NullPointerException ex) {
                    throw new FlickrEntityException("Wrong method called or paramaters not set.", ex);
                }
		return finalURL;
	}
        
        /**
         * Downloads the API response from Flickr
         * @param isEmail   true, if 'value' is email | false if 'value' is displeyName
         * @throws FlickrEntityException 
         */
	private void getData(boolean isEmail) throws FlickrEntityException {
		try {
			data = Downloader.download(getUrl(isEmail));
		} catch (DownloaderException ex) {
			throw new FlickrEntityException("Downloading of user data failed.", ex);
		}
	}

}
