package cz.muni.fi.pb138.flickrgraphr.api.dbquery;

import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.basex.server.ClientQuery;
import org.basex.server.ClientSession;

/**
 * Represents one DB query needed to process request on Graphr API Gets counts
 * of uploaded photos in given period
 *
 * @author Martin Ukrop, Jan Drábek
 */
public class UploadedPhotos extends AbstractDatabaseQuery {

	// parameters for XQuery
	private String beginDate;
	private String endDate;

	/**
	 * Create instance of query
	 * @param context 
	 */
	public UploadedPhotos(ServletContext context) {
		this.context = context;
	}

	@Override
	public String execute() throws DatabaseQueryException {
		// Get database
		ClientSession session;
		try {
			session = getDatabaseSession().get("uploaded-photos");
		} catch (NoDatabaseException ex) {
			throw new DatabaseQueryException("No database connection", ex);
		}
		// Get query
		String queryContent = getQuery("/xml/xquery/api_uploaded_photos.xq");
		// Query the database
		String output;
		try {
			ClientQuery query = session.query(queryContent);
			query.bind("begin_date", beginDate);
			query.bind("end_date", endDate);
			output = query.execute();
			session.close();
			return output;
		} catch (IOException ex) {
			throw new DatabaseQueryException("Problem with executing xquery 'count of uploaded photos'.", ex);
		}
	}

	@Override
	public void setParameter(String name, String value) throws DatabaseQueryException {
		if (name.equals("beginDate")) {
			beginDate = value;
		} else if (name.equals("endDate")) {
			endDate = value;
		} else {
			throw new DatabaseQueryException("Parameter " + name + "does not exist.");
		}
	}
}
