package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Subject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "CoursesServlet", value = "/courses")
public class CoursesServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.subjectDao = new EntityDao<>(dbConnection);
        } catch (Exception e) {
            throw new ServletException("Error initializing CoursesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sort = req.getParameter("sort");
        List<Subject> subjects;
        try {
            subjects = subjectDao.findAll(Subject.class).stream()
                    .filter(Subject::getActive)
                    .sorted(getComparator(sort))
                    .collect(Collectors.toList());
            req.setAttribute("subjects", subjects);
            req.getRequestDispatcher("/views/courses.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Comparator<Subject> getComparator(String sort) {
        if ("teacher".equals(sort)) {
            return Comparator.comparing(subject -> subject.getTeacher().getName());
        } else {  // Default to sort by subject name
            return Comparator.comparing(Subject::getSubjectName);
        }
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

