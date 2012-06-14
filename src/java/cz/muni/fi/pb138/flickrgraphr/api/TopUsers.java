package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.dbquery.AddUser;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQuery;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQueryException;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.GetUserId;
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

/**
 * Graphr API - page which serves top users data For details
 *
 * @see http://code.google.com/p/flickr-graphr/wiki/GraphrAPI
 * @author Martin Ukrop, manteaxx
 */
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

            //validate input parameters---------------------------------
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
            //---------------------------------------------------------

            //get input type (e-mail || display name || flickr ID)
            IdType authType = Validator.getIdType(userIdentification);

                        User user = null;
                        FlickrEntity entity = null;

            try {
                switch (authType) {
                    case email:
                        /*
                         * - dotaz na Flickr, aby si zistil ID
                         * (GetUser.fromEmail) - ak user neexistuje, vracias
                         * Vaskovi user does not exist - ak esxituje, vlozis
                         * jeho displayName do DB (AddUser) - dotaz na
                         * topUsersData, vracias Vaskovi JSON
                         */

                        entity = new GetUser(getServletContext(), userIdentification, true);
                        user = ((GetUser) entity).fromEmail();

                        //user not exist => error json
                        if (!user.isValid()) {
                            out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.UserNotExist));
                            return;
                        }

                        //user exists => insert into DB
                        AddUser(user);
                        break;

                    case name:
                        /*
                         * - dotaz na nasu databazu, ci tohto usera uz mame
                         * (getUserId) - ak mame, dotaz na topUsersData, vracias
                         * Vaskovi JSON - ak nemame, dotaz na Flickr, aby si
                         * zistil ID (GetUser.fromName) - ak user neexistuje,
                         * vracias Vaskovi user does not exist - ak esxituje,
                         * vlozis jeho displayName do DB (AddUser) - dotaz na
                         * topUsersData, vracias Vaskovi JSON
                         */

                        GetUserId getUserId = new GetUserId(getServletContext());
                        getUserId.setParameter("displayName", userIdentification);
                        String uId = getUserId.execute();

                        //no ID has this display name
                        if (uId == null || uId.isEmpty()) {
                            entity = new GetUser(getServletContext(), userIdentification, false);
                            user = ((GetUser) entity).fromName();
                            if (user.isValid()) {
                                AddUser(user);
                            } else {
                                out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.UserNotExist));
                                return;
                            }
                        } else {
                            //user is in xml db, just create instance
                            user = new User(uId, userIdentification);
                        }

                        break;

                    case flickrId:
                        /*
                         * - (tu by to malo byt inak, ale strasne by sa to tym
                         * komplikovalo, takze to bude len takto jednoducho) -
                         * predpokladas, ze ID je vzdy spravne (existujuce),
                         * teda nepytas sa Flickra ani nikoho ineho - rovno
                         * dotaz na topUsersData, vracias Vaskovi JSON
                         */
                        user = new User(userIdentification, null);
                        break;
                }
            } catch (FlickrEntityException ex) {
                Logger.getLogger(TopUsers.class.getName()).log(Level.SEVERE, null, ex);
                out.println(ex.toString());
                return;
            }


            //user was not found => error json response
            if (user.getId() == null) {
                out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.UserNotExist));
                return;
            }

            //get top users from xml database-----------------------------
            DatabaseQuery topUsersQuery =
                    new cz.muni.fi.pb138.flickrgraphr.api.dbquery.TopUsers(getServletContext());

                        topUsersQuery.setParameter("beginDate", startDate);
                        topUsersQuery.setParameter("endDate", endDate);
                        topUsersQuery.setParameter("userId", user.getId());

            out.println(topUsersQuery.execute());
            //-------------------------------------------------------------

        } catch (DatabaseQueryException ex) {
            if (ex.getCause() instanceof NoDatabaseException) {
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

    
    /**
     * This method creates new user in xml database
     * @param user instance of User with flickr id and flickr display name
     * @throws DatabaseQueryException 
     */
    private void AddUser(User user) throws DatabaseQueryException {
        AddUser(user.getId(), user.getDisplayName());
    }

    /**
     * This method creates new user in xml database
     * @param id flicker id
     * @param displayName flicker display name
     * @throws DatabaseQueryException 
     */
    private void AddUser(String id, String displayName) throws DatabaseQueryException {
        AddUser aU = new AddUser(getServletContext());
        aU.setParameter("userId", id);
        aU.setParameter("displayName", displayName);
        aU.execute();
    }
}