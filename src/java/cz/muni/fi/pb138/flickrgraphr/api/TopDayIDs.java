/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.api;

import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQuery;
import cz.muni.fi.pb138.flickrgraphr.api.dbquery.DatabaseQueryException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mantaexx
 */
public class TopDayIDs extends HttpServlet {

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
            
            String countString = request.getParameter("count");
            String date = request.getParameter("date");

            if (countString == null || date == null) {
                out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.IncorrectParameters));
                return;
            }


            try { //just to check if it really is integer
                Integer.parseInt(countString);
            } catch (NumberFormatException ex) {
                out.println(JsonBuilder.getErrorJsonForError(JsonBuilder.errorType.IncorrectParameters));
                Logger.getLogger(TopDayIDs.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            DatabaseQuery query = new cz.muni.fi.pb138.flickrgraphr.api.dbquery.TopIdsForDay(getServletContext());
            try {
                query.setParameter("count", countString);
                query.setParameter("date", date);

            } catch (DatabaseQueryException ex) {
                Logger.getLogger(TopDayIDs.class.getName()).log(Level.SEVERE, null, ex);
                response.setStatus(500);
                return;
            }
            try {
                out.println(query.execute());
            } catch (DatabaseQueryException ex) {
                Logger.getLogger(TopDayIDs.class.getName()).log(Level.SEVERE, null, ex);
                response.setStatus(500);
                return;
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
