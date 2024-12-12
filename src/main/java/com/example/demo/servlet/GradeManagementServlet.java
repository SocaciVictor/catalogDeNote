package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Grade;
import com.example.demo.persistence.entities.StudentsSubject;
import com.example.demo.persistence.entities.Subject;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(name = "GradeManagementServlet", urlPatterns = {"/manageGrade"})
public class GradeManagementServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Grade> gradeDao;
    private EntityDao<Subject> subjectDao;
    private EntityDao<User> userDao;
    private EntityDao<StudentsSubject> studentsSubjectDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.gradeDao = new EntityDao<>(dbConnection);
            this.subjectDao = new EntityDao<>(dbConnection);
            this.userDao = new EntityDao<>(dbConnection);
            this.studentsSubjectDao = new EntityDao<>(dbConnection);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");


        try {

            if (!action.equals("add") ) {
                int gradeId = Integer.parseInt(req.getParameter("id"));
                Grade grade = gradeDao.findById(Grade.class, gradeId);
                req.setAttribute("grade", grade);
                req.setAttribute("action", action);
                // Depending on the action, forward to different JSP pages
                if ("edit".equals(action)) {
                    req.getRequestDispatcher("/views/grade.jsp").forward(req, resp);
                } else if ("delete".equals(action)) {
                    req.getRequestDispatcher("/views/grade.jsp").forward(req, resp);
                }else
                    req.getRequestDispatcher("/views/viewGrade.jsp").forward(req, resp);
                }else {
                List<Subject> subjects = subjectDao.findTeacherCoursesById(Subject.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
                List<User> users = userDao.findAllStudentsByTeacherId(User.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
                req.setAttribute("subjects", subjects);
                req.setAttribute("students", users);
                req.setAttribute("action", action);
                req.getRequestDispatcher("/views/grade.jsp").forward(req, resp);
            }
            } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                // Retrieve and validate form inputs
                String gradeValue = req.getParameter("gradeValue");
                String studentSubjectId = req.getParameter("studentId");
                String subjectId = req.getParameter("subjectId");

                if (gradeValue == null || studentSubjectId == null) {
                    throw new IllegalArgumentException("Grade value and student-subject ID are required.");
                }

                // Create Grade entity
                Grade grade = new Grade();
                grade.setGradeValue(new BigDecimal(gradeValue));

                // Link the Grade to an existing StudentsSubject
                StudentsSubject studentsSubject = studentsSubjectDao.findByStudentIdAndSubjectId(Integer.parseInt(studentSubjectId), Integer.parseInt(subjectId));
                if (studentsSubject == null) {

                }
                grade.setStudentSubject(studentsSubject);
                grade.setId(gradeDao.findAll(Grade.class).size()+1);

                // Set additional fields
                grade.setAddedDate(new Timestamp(System.currentTimeMillis()));
                grade.setActive(true);

                // Persist the grade
                gradeDao.addGrade(grade);

                // Redirect to grades page or success message
                resp.sendRedirect("grades");
            } else if ("edit".equals(action)) {
                // Implement editing logic
                Grade grade = gradeDao.findById(Grade.class, Integer.parseInt(req.getParameter("id")));
                grade.setGradeValue(new BigDecimal(req.getParameter("gradeValue")));
                gradeDao.editGrade(grade);
                resp.sendRedirect("grades");
            } else if ("delete".equals(action)) {
                // Implement delete logic
                Grade grade = gradeDao.findById(Grade.class, Integer.parseInt(req.getParameter("id")));

                gradeDao.deleteGrade(grade);
                resp.sendRedirect("grades");
            }
        } catch (Exception e) {
            // Handle exceptions and send error response if needed
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error processing request: " + e.getMessage());
        }
    }
}
