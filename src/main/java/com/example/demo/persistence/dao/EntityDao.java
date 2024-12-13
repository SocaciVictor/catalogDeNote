package com.example.demo.persistence.dao;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.PersistableEntity;
import com.example.demo.persistence.entities.Subject;

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
    public T findByStudentIdAndSubjectId(int studentId ,int subjectId) throws Exception {
        return connection.findByStudentIdAndSubjectId(studentId, subjectId);
    }

    public List<T> findAllStudents(Class<T> entityType, String userType) throws Exception {
        return connection.findAllStudents(entityType, userType);
    }

    public List<T> findAllGrades(Class<T> entityType) throws Exception {
        return connection.findAllGrades(entityType);
    }

    public void delete(int subjectId) throws Exception {
        connection.deleteSubject(subjectId);
    }

    public Subject findBySubjectByName(String subjectName) throws Exception {
        return connection.findBySubjectByName(subjectName);
    }

    public void addSubject(String subjectName, int teacherId,int[] studentIds) throws Exception {
        connection.addSubject(subjectName, teacherId, studentIds);
    }

    public List<T> findAllStudentsByTeacherId(Class<T> entitype, int teacherId) throws Exception {
        return connection.findAllStudentsByTeacherId(entitype, teacherId);
    }

    public void addGrade(T grade) throws Exception {
        connection.addGrade(grade);
    }

    public void deleteGrade(T gradeEntity) throws Exception {
        connection.deleteGrade(gradeEntity);
    }

    public void editGrade(T grade) throws Exception {
        connection.editGrade(grade);
    }
    public void editSubject(int subjectId, String newSubjectName, int[] studentIds) throws Exception{
        connection.editSubject(subjectId, newSubjectName, studentIds);
    }

    public List<T> findSubjectByStudentId(int studentId) throws Exception {
        return connection.findAllSubjectByStudentId(studentId);
    }
}
