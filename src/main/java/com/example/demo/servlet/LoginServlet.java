package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;

@WebServlet(name="IndexServlet", urlPatterns = {"/", "/login/*"})
public class LoginServlet extends HttpServlet {
    private Connection connection;
    private EntityDao<User> userDao;

    @Override
    public void init() throws ServletException {
        try {
            // Initialize DatabaseConnection and EntityDao
            /* Check resources -> META-INF -> persistence.xml */
            this.connection = new DatabaseConnection("default");
            this.userDao = new EntityDao<>(connection);
        } catch (Exception e) {
            throw new ServletException("Error initializing RegisterServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (Objects.isNull(currentUser)) {
            getServletContext().getRequestDispatcher("/views/login.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/homeServlet");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // Retrieve email and password from the form
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Find the user by email and password
        User foundUser = null;
        try {
            foundUser = userDao.findFirstByParams(User.class,
                    new ParameterPair<>("email", email),
                    new ParameterPair<>("password", password));
        } catch (Exception e) {
            throw new ServletException("Error during authentication", e);
        }

        // Check if the user was found and set session attribute
        if (Objects.nonNull(foundUser)) {
            session.setAttribute("currentUser", foundUser);
            session.setAttribute("userType", foundUser.getUserType());
            resp.sendRedirect("/homeServlet");  // Redirect to home if login successful
        } else {
            req.setAttribute("error", "Invalid email or password.");
            getServletContext().getRequestDispatcher("/views/login.jsp").forward(req, resp); // Redirect back to login
        }
    }

    @Override
    public void destroy() {
        try {
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
