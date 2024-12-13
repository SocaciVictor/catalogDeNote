package com.example.demo.servlet;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import com.example.demo.persistence.entities.Grade;
import com.example.demo.persistence.entities.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentGrades", urlPatterns = "/studentGrades")
public class StudentGrades extends HttpServlet {
    private Connection dbConnection;
    private EntityDao<Grade> gradeDao;
    private EntityDao<User> userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try
        {
            this.dbConnection = new DatabaseConnection("default");
            this.gradeDao = new EntityDao<>(dbConnection);
            this.userDao = new EntityDao<>(dbConnection);
        }catch (Exception e){
            throw new ServletException("Error initializing MyCoursesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Grade> gradeList;
        try{
            int studentId = (int) req.getSession().getAttribute("currentUser").getClass().getMethod("getId").invoke(req.getSession().getAttribute("currentUser"));
            gradeList = gradeDao.findAllGradesByStudentId(Grade.class, studentId);
            req.setAttribute("gradeList", gradeList);
            req.getRequestDispatcher("/views/studentGrades.jsp").forward(req, resp);
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
