package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQuery;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQueryException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Graphr API - page which serves top tags from week period
 * For details @see http://code.google.com/p/flickr-graphr/wiki/GraphrAPI
 * @author Jan Drábek
 */
public class TopTags extends HttpServlet {

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// Set proper encoding and prepare to output
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String tag = null;
			String method = null;
			// Fetch parameters
			try {
				tag = request.getParameter("tag").toString();
				method = request.getParameter("method").toString();
				if (!method.equals("our") && !method.equals("their")) {
					throw new IllegalArgumentException();
				}
				if(tag.equals("")) {
					throw new IllegalArgumentException();
				}
				// Query the database
				DatabaseQuery query = new cz.muni.fi.pb138.flickrgraphr.api.dbquery.TopTags(getServletContext());
				try {
					query.setParameter("tag", tag);
					query.setParameter("method", method);
					out.println(query.execute());
				} catch (DatabaseQueryException ex) {
					if(ex.getCause() instanceof NoDatabaseException) {
						response.setStatus(500);
						return;
					}
					out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.IncorrectParameters));
				}
			} catch (NullPointerException ex) {
				out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.IncorrectParameters));
			} catch (IllegalArgumentException ex) {
				out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.IncorrectParameters));
			}
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}