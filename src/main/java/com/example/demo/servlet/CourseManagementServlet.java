package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
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
import java.util.List;

@WebServlet(name = "CourseManagementServlet", urlPatterns = {"/manageCourse"})
public class CourseManagementServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;
    private EntityDao<User> userDao;
    private EntityDao<StudentsSubject> studentsSubjectDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
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
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int courseId = Integer.parseInt(req.getParameter("id"));


        try {
            if ("update".equals(action)) {
                // Fetch the current subject and update its name
                Subject subject = subjectDao.findById(Subject.class, courseId);
                subject.setSubjectName(req.getParameter("subjectName"));
               // subjectDao.update(subject);

                // Update enrolled students:
                // Step 1: Clear existing enrollments for this course
//                List<StudentsSubject> enrollments = studentsSubjectDao.findAllByCourseId(User.class,courseId);
//                for (StudentsSubject enrollment : enrollments) {
//                    studentsSubjectDao.delete(enrollment);
//                }

                // Step 2: Add new enrollments based on selected students in the form
                String[] studentIds = req.getParameterValues("students");
                if (studentIds != null) {
                    for (String studentId : studentIds) {
                        StudentsSubject newEnrollment = new StudentsSubject();
                        newEnrollment.setStudent(userDao.findById(User.class, Integer.parseInt(studentId)));
                        newEnrollment.setSubject(subject);
                     //  studentsSubjectDao.create(newEnrollment);
                    }
                }

                resp.sendRedirect("myCourses");
            } else if ("delete".equals(action)) {
                Subject subject = subjectDao.findById(Subject.class, courseId);
                subjectDao.delete(subject);
                resp.sendRedirect("myCourses");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

}


