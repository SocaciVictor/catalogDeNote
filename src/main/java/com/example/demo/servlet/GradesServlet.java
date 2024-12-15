package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Grade;
import com.example.demo.persistence.entities.Subject;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "GradesServlet", urlPatterns = "/grades")
public class GradesServlet extends HttpServlet {

    private Connection dbConnection;
    private EntityDao<Grade> gradeDao;
    private EntityDao<Subject> subjectDao;
    private EntityDao<User> userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbConnection = new DatabaseConnection("default");
            this.gradeDao = new EntityDao<>(dbConnection);
            this.subjectDao = new EntityDao<>(dbConnection);
            this.userDao = new EntityDao<>(dbConnection);
        }catch (Exception e){
            throw new ServletException("Error initializing GradesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sort = req.getParameter("sort");
        try {
            List<Grade> grades = gradeDao.findAllGrades(Grade.class);
            List<Subject> subjects = subjectDao.findTeacherCoursesById(Subject.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
            List<User> users = userDao.findAllStudentsByTeacherId(User.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
            if ("date".equals(sort)) {
                grades.sort(Comparator.comparing(Grade::getAddedDate));
            } else if ("student".equals(sort)) {
                grades.sort(Comparator.comparing(g -> g.getStudentSubject().getStudent().getName()));
            } else if ("subject".equals(sort)) {
                grades.sort(Comparator.comparing(g -> g.getStudentSubject().getSubject().getSubjectName()));
            }
            req.setAttribute("grades", grades);
            req.setAttribute("subjects", subjects);
            req.setAttribute("students", users);
            req.setAttribute("teacherId", (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
            req.getRequestDispatcher("/views/grades.jsp").forward(req, resp);
        }catch (Exception e){
            throw new RuntimeException(e);
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

