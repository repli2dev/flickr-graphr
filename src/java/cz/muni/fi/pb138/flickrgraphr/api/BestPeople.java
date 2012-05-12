package cz.muni.fi.pb138.flickrgraphr.api;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BestPeople extends HttpServlet {

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
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BestPeople</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BestPeople at " + request.getContextPath() + "</h1>");
            out.println("hooray");

            try {
                String user = request.getParameter("name").toString();
                String startDate = request.getParameter("start-date").toString();
                String endDate = request.getParameter("end-date").toString();

                out.println("parameter user: " + user + "<br />");

                if (Validator.isDate(startDate)) {
                    out.println(startDate);
                } else {
                    out.println(JsonBuilder.getErrorJson(2, "invalid start date"));
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
                }

                out.println("user id is: " + userId);





            } catch (NullPointerException ex) {
                out.print(JsonBuilder.getErrorJson(2, "missing parameter"));
            }

            out.println("</body>");
            out.println("</html>");
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
