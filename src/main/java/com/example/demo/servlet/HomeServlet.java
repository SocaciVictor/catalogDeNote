package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Subject;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "HomeServlet", value = "/homeServlet")
public class HomeServlet extends HttpServlet {

    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;

    @Override
    public void init() throws ServletException {
        try {
            // Initialize DatabaseConnection and EntityDao
            /* Check resources -> META-INF -> persistence.xml */
            this.dbConnection = new DatabaseConnection("default");
            this.subjectDao = new EntityDao<>(dbConnection);
        } catch (Exception e) {
            throw new ServletException("Error initializing RegisterServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the current user from the session
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        // Check if the user is logged in
        if (currentUser == null) {
            response.sendRedirect("login");  // Redirect to login page if not logged in
            return;
        }

        // Set the user as an attribute for the JSP
        request.setAttribute("username", currentUser.getName());

        // Forward to the home JSP page
        getServletContext().getRequestDispatcher("/views/home.jsp").forward(request, response);
    }


    @Override
    public void destroy() {
        try {
            this.dbConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}