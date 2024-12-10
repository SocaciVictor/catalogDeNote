package com.example.demo.persistence.dao;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.PersistableEntity;

import java.util.List;

public class EntityDao<T extends PersistableEntity> {
    private final Connection connection;
    public EntityDao(Connection connection) {
        this.connection = connection;
    }

    public void save(T object) throws Exception {
       connection.save(object);
    }

    public List<T> findAll(Class<T> entityType) throws Exception{
       return connection.findAll(entityType);
    }

    public T findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception{
        return connection.findFirstByParams(entityType, params);
    }

    public List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception {
        return connection.findAllByParams(entityType, params);
    }

    public List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception {
        return connection.findTeacherCoursesById(entityType, teacherId);
    }

    public T findById(Class<T> subjectClass, int courseId) throws Exception {
        return connection.findById(subjectClass, courseId);
    }

    public List<T> findAllStudents(Class<T> entityType, String userType) throws Exception {
        return connection.findAllStudents(entityType, userType);
    }

    public List<T> findAllGrades(Class<T> entityType) throws Exception {
        return connection.findAllGrades(entityType);
    }
}
