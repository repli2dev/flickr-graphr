package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQuery;
import java.io.IOException;
import java.io.PrintWriter;
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
            /*
             * TODO output your page here. You may use following sample code.
             */
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet BestPeople</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet BestPeople at " + request.getContextPath() + "</h1>");
//            out.println("hooray");

            String user = request.getParameter("name");
            String startDate = request.getParameter("start-date");
            String endDate = request.getParameter("end-date");

            if (user == null
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

            IdType authType = Validator.getIdType(user);

            String userId = null;

            switch (authType) {
                case email:
                    userId = FlickAPI.getIdFromEmail(user);
                    break;
                case name:
                    userId = FlickAPI.getIdFromName(user);
                    break;
                case flickrId:
                    userId = user;
                    break;
            }

            out.println("user id is: " + userId);

            out.println();
            
            
            
            
            DatabaseQuery query = 
                    cz.muni.fi.pb138.flickrgraphr.api.dbquery.TopUsers(getServletContext());




//            out.println("</body>");
//            out.println("</html>");

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
