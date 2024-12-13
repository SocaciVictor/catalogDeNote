package com.example.demo.persistence.connection;

import com.example.demo.persistence.entities.PersistableEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileSerializeConnection extends Connection {
    private String filename;

    public FileSerializeConnection(String filename) {
        this.filename = filename;
    }

    @Override
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        List<T> entites = findAll((Class<T>) entity.getClass());
        entites.add(entity);
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.filename))){
            out.writeObject(entites);
        }
    }

    @Override
    public <T extends PersistableEntity> T findBySubjectByName(String subjectName) throws Exception {
        return null;
    }

    @Override
    public <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<T>) in.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGrades(Class<T> entityType) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllStudentsByTeacherId(Class<T> entityType, int teacherId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllStudents(Class<T> entityType, String userType) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> T findById(Class<T> entityType, int id) throws Exception {
        return null;
    }

    @Override
    public <T extends PersistableEntity> List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair... params) throws Exception {
        // Load all entities from the file
        List<T> entities = findAll(entityType);

        // Filter entities by parameters and return the first match
        return entities.stream()
                .filter(entity -> matchesParams(entity, params))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair... params) throws Exception {
        // Load all entities from the file
        List<T> entities = findAll(entityType);

        // Filter entities by parameters and return the first match
        return entities.stream()
                .filter(entity -> matchesParams(entity, params))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectStudentsBySubjectId(Class<T> entityType, int subjectId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectStudentsByStudentId(Class<T> entityType, int studentId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectByStudentId(int studentId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGradesByStudentId(Class<T> entityType, int studentId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGradesBySubjectId(Class<T> entityType, int subjectId) throws Exception {
        return List.of();
    }

    @Override
    public <T extends PersistableEntity> void deleteSubject(int subjectId) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> T findByStudentIdAndSubjectId(int studentId, int subjectId) throws Exception {
        return null;
    }

    @Override
    public <T extends PersistableEntity> void delete(T entity) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> void addGrade(T grade) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> void deleteGrade(T grade) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> void editGrade(T grade) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> void editSubject(int subjectId, String newSubjectName, int[] studentsId) throws Exception {

    }



    @Override
    public <T extends PersistableEntity> void addSubject(String subjectName, int teacherId, int[] studentIds) throws Exception {
        super.addSubject(subjectName, teacherId, studentIds);
    }

    @Override
    public void close() throws IOException {
        /* The file will close on CRUD operations */
    }

    /* HELPER METHOD */
    private <T extends PersistableEntity> boolean matchesParams(T entity, ParameterPair... params) {
        try {
            for (ParameterPair<?, ?> param : params) {
                String paramName = (String) param.getName();
                Object paramValue = param.getValue();

                // Use reflection to access the field's value
                var field = entity.getClass().getDeclaredField(paramName);
                field.setAccessible(true);
                Object fieldValue = field.get(entity);

                // Check if the field value matches the parameter value
                if (fieldValue == null || !fieldValue.equals(paramValue)) {
                    return false; // If any field doesn't match, return false
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false; // If there's an error accessing the field, assume no match
        }
        return true; // All parameters matched
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
