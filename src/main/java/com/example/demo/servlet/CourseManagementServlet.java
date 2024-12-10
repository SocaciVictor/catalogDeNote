package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Subject;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CourseManagementServlet", urlPatterns = {"/manageCourse"})
public class CourseManagementServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;
    private EntityDao<User> userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.subjectDao = new EntityDao<>(dbConnection);
            this.userDao = new EntityDao<>(dbConnection);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int courseId = Integer.parseInt(req.getParameter("id"));

        try {
            Subject subject = subjectDao.findById(Subject.class, courseId);
            req.setAttribute("subject", subject);
            req.setAttribute("enrolledStudents", userDao.findAllStudents(User.class, "student"));
            req.setAttribute("action", action);
            req.getRequestDispatcher("/views/course.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching course details");
        }
    }
}


