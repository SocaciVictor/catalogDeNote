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
import java.util.List;

@WebServlet(name = "CoursesServlet", value = "/courses")
public class CoursesServlet extends HttpServlet {

    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;


    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.subjectDao = new EntityDao<>(dbConnection);
        }catch (Exception e){
            throw new ServletException("Error initializing CoursesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Subject> subjects = null;
        try {
            subjects = subjectDao.findAll(Subject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("subjects", subjects);
        req.getRequestDispatcher("/views/courses.jsp").forward(req, resp);
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
