package com.example.demo.persistence.connection;

import com.example.demo.persistence.entities.PersistableEntity;

import java.io.Closeable;
import java.util.List;

public abstract class Connection implements Closeable {
    public abstract <T extends PersistableEntity> void save(T entity) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllGrades(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllStudents(Class<T> entityType, String userType) throws Exception;
    public abstract <T extends PersistableEntity> T findById(Class<T> entityType, int id) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception;
    public abstract <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    /* TODO: Add Update & Delete */
}
