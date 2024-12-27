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

@WebServlet(name = "MyCoursesServlet", value = "/myCourses")
public class MyCoursesServlet extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Subject> subjectDao;
    private EntityDao<User> userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try
        {
            this.dbConnection = new DatabaseConnection("default");
            this.subjectDao = new EntityDao<>(dbConnection);
            this.userDao = new EntityDao<>(dbConnection);
        }catch (Exception e){
            throw new ServletException("Error initializing MyCoursesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Subject> subjects = null;
        try {
            subjects = subjectDao.findTeacherCoursesById(Subject.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser")));
            System.out.println(subjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("subjects", subjects);
        req.getRequestDispatcher("/views/myCourses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subjectName = req.getParameter("subjectName");
        Subject subjectOld = null;
        try {
            subjectOld = subjectDao.findBySubjectByName(subjectName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (subjectOld == null) {
        try{
            Subject subject = new Subject();
            subject.setActive(true);
            subject.setSubjectName(subjectName);
            subject.setId(subjectDao.findAll(Subject.class).size()+1);
            subject.setTeacher(userDao.findById(User.class, (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser"))));
            subjectDao.save(subject);

            resp.sendRedirect("homeServlet");
        }catch (Exception e){
            throw new ServletException("Error initializing MyCoursesServlet", e);
        }
        }else
        {
            resp.sendRedirect("homeServlet");
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
