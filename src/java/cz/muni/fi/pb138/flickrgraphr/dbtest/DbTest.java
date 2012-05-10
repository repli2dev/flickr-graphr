package cz.muni.fi.pb138.flickrgraphr.dbtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;
import org.apache.commons.io.FileUtils;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.BaseXSession;
import java.io.*;
import javax.xml.validation.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.XMLConstants;

/**
 * Test class working with BaseX database
 * @author Jan Drabek, Martin Ukrop
 * 
 * To run this file, following conditions must be met:
 *      - BaseX server must be running
 *      - rootPath must be correctly set (line ~30)
 */

public class DbTest {
	public static void main(String [] args) throws IOException {
                
                // Set your own path for flickr-graphr directory
                String rootPath = "/home/martin/Documents/flickr-graphr/";
                //String rootPath = "/home/jan/TEMP/flickr-graphr/";
                
                // Set other paths
                // String dbPath = rootPath+"src/db/";
                String docPath = rootPath+"src/xml-data/temp3/";
                String xqPath = rootPath+"src/xml-data/xq2/";
                String xsdPath = rootPath+"src/xml-data/xsd2/";
                String xsltPath = rootPath+"src/xml-data/xslt2/";
                String apiPath = rootPath+"src/xml-data/api_original/";
                
                BaseXSession bxs = new BaseXSession("localhost",1984,"admin","admin");
                ClientSession cs;
                //cs = bxs.get("users",true);
                //BaseXSession.enableWriteback(cs);
                //cs = bxs.get("top_photos",true);
                //BaseXSession.enableWriteback(cs);
                //cs = bxs.get("top_users",true);
                //BaseXSession.enableWriteback(cs);
                
		// Add documents with photos (temporary solution)
                /*
                cs = bxs.get("top_photos");
                for(int i=17; i<=30; i++) {
                       cs.execute("ADD "+docPath+"top_photos_2012-04-"+i+".xml");
                }*/
                
                // Count data for top users
                
                /*cs = bxs.get("top_users");
                for (int i=24; i<=30; i++) {
                    cs.execute("ADD TO top_users_2012-04-"+i+".xml <root/>");
                    // Read query from file
                    String query = FileUtils.readFileToString(new File(xqPath +
                        "topusers_from_topphotos.xq"), "UTF-8");
                    ClientQuery cq = cs.query(query);
                    // Bind needed variables, run query
                    cq.bind("date_processed", "2012-04-"+i);
                    cq.bind("max_delay", "7");
                    cq.bind("output_file", "top_users/top_users_2012-04-"+i+".xml");
                    System.out.println(cq.execute());
                }*/
                
                // for transforming outputStream into inputStream see
                // http://stackoverflow.com/questions/5778658/java-converting-from-outputstream-to-inputstream
                
                cs = bxs.get("users");
                TransformerFactory tfactory = TransformerFactory.newInstance();
                Source xslt = new StreamSource(new File(xsltPath+"flickr_interestingness_to_users.xslt"));
                SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                OutputStream tempOutput = new ByteArrayOutputStream();
                
                Source dataSource = new StreamSource(new File(apiPath+"api_best_photos_2012-04-30_invalid.xml"));
                Result dataResult = new StreamResult(tempOutput);
                try {
                    Schema schema = sFactory.newSchema(new File(xsdPath+"flickr_interestingness.xsd"));
                    Validator validator = schema.newValidator();
                    validator.validate(dataSource);
                    Transformer transformer = tfactory.newTransformer(xslt);
                    transformer.setParameter("DATE", "2012-04-30");
                    //transformer.setParameter("DATE", "2012-04-31");
                    transformer.transform(dataSource, dataResult);
                    InputStream tempInput = new ByteArrayInputStream(
                        ((ByteArrayOutputStream) tempOutput).toByteArray());
                    cs.add("users_2012-04-30.xml", tempInput);
                } catch (Exception e) {
                    System.err.println("Error happened:\n" + e.getMessage());
                }
                
                
		// close connection
                //cs.close();
	}
}

/*
 * context: connection 'ClientSession session'
 *          date 'String date'
 * to do:
 *  - construct Flickr API query
 *  - run Flickr API query
 *  - valitade against 'flickr_interestingness.xsd'
 *     - if failed, log message, save original API output and exit
 * 
 *  - run transformation 'flickr_interestingness2best_photos.xslt'
 *  - if exception thrown
 *     - log message & result contents
 *  - ADD result into DB best_photos/date.xml
 *  - run XQuery 'compute_people_for_date.xq'
 * 
 *  - run transformation 'flickr_interestingness2users.xslt'
 *  - if exception thrown
 *     - log message & result contents
 *  - ADD result into DB users/date.xml
 */