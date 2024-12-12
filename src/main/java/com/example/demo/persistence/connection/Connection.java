package com.example.demo.persistence.connection;

import com.example.demo.persistence.entities.PersistableEntity;

import java.io.Closeable;
import java.util.List;

public abstract class Connection implements Closeable {
    public abstract <T extends PersistableEntity> void save(T entity) throws Exception;
    public abstract <T extends PersistableEntity> T findBySubjectByName(String subjectName) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllGrades(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllStudentsByTeacherId(Class<T> entityType, int teacherId) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllStudents(Class<T> entityType, String userType) throws Exception;
    public abstract <T extends PersistableEntity> T findById(Class<T> entityType, int id) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception;
    public abstract <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllSubjectStudentsBySubjectId(Class<T> entityType, int subjectId) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllSubjectStudentsByStudentId(Class<T> entityType, int studentId) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllGradesByStudentId(Class<T> entityType, int studentId) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllGradesBySubjectId(Class<T> entityType, int subjectId) throws Exception;
    public abstract  <T extends PersistableEntity> T findByStudentIdAndSubjectId(int studentId, int subjectId) throws Exception;

    /* TODO: Add Update & Delete */

    public abstract <T extends PersistableEntity> void deleteSubject(int subjectId) throws Exception;
    public abstract <T extends PersistableEntity> void delete(T entity) throws Exception;

    public abstract <T extends PersistableEntity> void addGrade(T grade) throws Exception;
    public abstract <T extends PersistableEntity> void deleteGrade(T grade) throws Exception;
    public abstract <T extends PersistableEntity> void editGrade(T grade) throws Exception;
    public abstract <T extends PersistableEntity> void editSubject(int subjectId, String newSubjectName, int[] studentsId) throws Exception;

    public <T extends PersistableEntity> void addSubject(String subjectName, int teacherId,int[] studentIds) throws Exception{
    }


}
