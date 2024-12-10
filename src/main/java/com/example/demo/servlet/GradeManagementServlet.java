package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Grade;
import com.example.demo.persistence.entities.StudentsSubject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "GradeManagementServlet", urlPatterns = {"/manageGrade"})
public class GradeManagementServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Grade> gradeDao;
    private EntityDao<StudentsSubject> studentsSubjectDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.gradeDao = new EntityDao<>(dbConnection);
            this.studentsSubjectDao = new EntityDao<>(dbConnection);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int gradeId = Integer.parseInt(req.getParameter("id"));

        try {
            Grade grade = gradeDao.findById(Grade.class, gradeId);
            if (grade != null) {
                req.setAttribute("grade", grade);
                req.setAttribute("action", action);
                // Depending on the action, forward to different JSP pages
                if ("edit".equals(action)) {
                    req.getRequestDispatcher("/views/grade.jsp").forward(req, resp);
                } else if ("delete".equals(action)) {
                    req.getRequestDispatcher("/views/grade.jsp").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/views/viewGrade.jsp").forward(req, resp);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Grade not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}
