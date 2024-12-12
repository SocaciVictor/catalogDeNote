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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


        if (action.equals("update")){
            try {
                int courseId = Integer.parseInt(req.getParameter("id"));
                Subject subject = subjectDao.findById(Subject.class, courseId);
                req.setAttribute("subject", subject);
                req.setAttribute("enrolledStudents", userDao.findAllStudents(User.class, "student"));
                req.setAttribute("action", action);
                req.getRequestDispatcher("/views/course.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching course details");
            }
        }else if (action.equals("add")){
            try {
                req.setAttribute("enrolledStudents", userDao.findAllStudents(User.class, "student"));
                req.setAttribute("action", action);
                req.getRequestDispatcher("/views/course.jsp").forward(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (action.equals("delete")) {
            int courseId = Integer.parseInt(req.getParameter("id"));
            Subject subject = null;
            try {
                subject = subjectDao.findById(Subject.class, courseId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            req.setAttribute("subject", subject);
            req.setAttribute("action", action);
            req.getRequestDispatcher("/views/course.jsp").forward(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String subjectName = req.getParameter("subjectName");
        try {
            if ("update".equals(action)) {
                int courseId = Integer.parseInt(req.getParameter("id"));

                String[] studentIds = req.getParameterValues("students");
                int[] studentIdsInt = Arrays.stream(studentIds)
                        .mapToInt(Integer::parseInt)
                        .toArray();
                subjectDao.editSubject(courseId, subjectName, studentIdsInt);
                resp.sendRedirect("myCourses");
            } else if ("delete".equals(action)) {
                int courseId = Integer.parseInt(req.getParameter("id"));
                Subject subject = subjectDao.findById(Subject.class, courseId);
                subjectDao.delete(courseId);
                resp.sendRedirect("myCourses");
            }else if ("add".equals(action)) {
                String[] studentIds = req.getParameterValues("students");
                int[] studentIdsInt = new int[studentsSubjectDao.findAll(StudentsSubject.class).size()];
                int teacherId = (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser"));
                if (studentIds != null) {
                    studentIdsInt = Arrays.stream(studentIds)
                            .mapToInt(Integer::parseInt)
                            .toArray();
                }
                subjectDao.addSubject(subjectName, teacherId, studentIdsInt);
                resp.sendRedirect("myCourses");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

}


