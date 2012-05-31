package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQuery;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQueryException;
import cz.muni.fi.pb138.flickrgraphr.backend.storage.NoDatabaseException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntity;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.FlickrEntityException;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.GetUser;
import cz.muni.fi.pb138.flickrgraphr.flickr.api.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TopUsers extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {


            String userIdentification = request.getParameter("name");
            String startDate = request.getParameter("start-date");
            String endDate = request.getParameter("end-date");

            if (userIdentification == null
                    || startDate == null
                    || endDate == null) {
                out.println(
                        JsonBuilder.getErrorJsonForError(
                        JsonBuilder.errorType.IncorrectParameters));
                return;
            }

            if (!Validator.isDate(startDate) || !Validator.isDate(endDate)) {
                out.println(
                        JsonBuilder.getErrorJsonForError(
                        JsonBuilder.errorType.IncorrectParameters));
                return;
            }

            IdType authType = Validator.getIdType(userIdentification);

            User user = null;
            FlickrEntity entity = null;

            try {
                switch (authType) {
                    case email:
                        //userId = FlickAPI.getIdFromEmail(user);
                        entity = new GetUser(getServletContext(), userIdentification, true);
                        user = ((GetUser) entity).fromEmail();
                        break;
                    case name:
                        entity = new GetUser(getServletContext(), userIdentification, false);
                        user = ((GetUser) entity).fromName();
                        break;
                    case flickrId:
                        user = new User(userIdentification, null);
                        break;
                }
            } catch (FlickrEntityException ex) {
                Logger.getLogger(TopUsers.class.getName()).log(Level.SEVERE, null, ex);
                out.println(ex.toString());
                return;
            }

            //out.println will be commented
            //out.println(user.getId() + ", " + user.getDisplayName());

            //user not found
            //TODO??
            if (user.getId() == null) {
                out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.UserNotExist));
                return;
            }

            DatabaseQuery topUsersQuery =
                    new cz.muni.fi.pb138.flickrgraphr.api.dbquery.TopUsers(getServletContext());


            topUsersQuery.setParameter("beginDate", startDate);
            topUsersQuery.setParameter("endDate", endDate);
            topUsersQuery.setParameter("userId", user.getId());

	    out.println(topUsersQuery.execute());


        } catch (DatabaseQueryException ex) {
		if(ex.getCause() instanceof NoDatabaseException) {
			response.setStatus(500);
			return;
		}
            Logger.getLogger(TopUsers.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
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
