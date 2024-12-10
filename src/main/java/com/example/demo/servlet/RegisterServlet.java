package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the registration page
        request.setAttribute("roles", List.of("student", "teacher"));
        request.getRequestDispatcher("views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        System.out.println(String.format("%s %s %s", username, email, password));

        // Create a new User entity
        User user = new User();
        try {
            user.setId(userDao.findAll(User.class).size()+1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserType(role);
        try {
            userDao.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        resp.sendRedirect("login");
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
