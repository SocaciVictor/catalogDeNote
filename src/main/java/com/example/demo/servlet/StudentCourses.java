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

@WebServlet(name = "StudentCourses", urlPatterns ="/studentCourses")
public class StudentCourses extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<User> userDao;
    private EntityDao<Subject> subjectDao;


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
        List<Subject> subjectsList;
        try {
            int studentId = (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser"));
            subjectsList = subjectDao.findSubjectByStudentId(studentId);
            req.setAttribute("studentsSubjects", subjectsList);
            req.getRequestDispatcher("/views/studentCourses.jsp").forward(req, resp);
        }catch (Exception e){
            throw new ServletException("Error initializing MyCoursesServlet", e);
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
