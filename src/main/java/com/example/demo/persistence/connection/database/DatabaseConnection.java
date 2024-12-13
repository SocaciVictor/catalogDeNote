package com.example.demo.persistence.connection.database;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.*;
import jakarta.persistence.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseConnection extends Connection {
    private DatabaseConnectionAbstract dbConnAbs;

    public DatabaseConnection(String persistenceUnit) {
        this.dbConnAbs = new DatabaseConnectionAbstract(persistenceUnit);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGradesByStudentId(Class<T> entityType, int studentId) throws Exception {
        if (!Grade.class.isAssignableFrom(entityType)) {
            throw new IllegalArgumentException("Entity type must be a Grade or its subclass.");
        }

        String jpql = "SELECT g FROM Grade g WHERE g.studentSubject.student.id = :studentId AND g.active = TRUE";
        return this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> query = entityManager.createQuery(jpql, entityType);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        }, List.class);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGradesBySubjectId(Class<T> entityType, int subjectId) throws Exception {
        if (!Grade.class.isAssignableFrom(entityType)) {
            throw new IllegalArgumentException("Entity type must be a Grade or its subclass.");
        }

        // Define the JPQL query to fetch grades linked to a specific subject through the StudentsSubject entity
        String jpql = "SELECT g FROM Grade g WHERE g.studentSubject.subject.id = :subjectId AND g.active = TRUE";
        return this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> query = entityManager.createQuery(jpql, entityType);
            query.setParameter("subjectId", subjectId);
            return query.getResultList();
        }, List.class);
    }


    @Override
    public <T extends PersistableEntity> void deleteSubject(int subjectId) throws Exception {
        try {
            dbConnAbs.executeStoredProcedure("deactivate_subject_and_related_entities", subjectId);
            System.out.println("Subject deactivated successfully.");
        } catch (Exception e) {
            System.err.println("Failed to deactivate subject: " + e.getMessage());
        }
    }

    @Override
    public <T extends PersistableEntity> void delete(T entity) throws Exception {

    }

    @Override
    public <T extends PersistableEntity> void editGrade(T grade) throws Exception {
        if (!(grade instanceof Grade)) {
            throw new IllegalArgumentException("Entity must be of type Grade");
        }

        dbConnAbs.executeTransaction(em -> {
            Grade gradeEntity = (Grade) grade;

            // Fetch the existing Grade from the database
            Grade existingGrade = em.find(Grade.class, gradeEntity.getId());
            if (existingGrade == null || !existingGrade.getActive()) {
                throw new IllegalStateException("Grade must exist and be active to edit.");
            }

            // Update fields (e.g., gradeValue)
            existingGrade.setGradeValue(gradeEntity.getGradeValue());
            existingGrade.setAddedDate(new Timestamp(System.currentTimeMillis()));

            // Persist changes
            em.merge(existingGrade);

            em.flush();
        });
    }


    @Override
    public <T extends PersistableEntity> void deleteGrade(T grade) throws Exception {
        if (!(grade instanceof Grade)) {
            throw new IllegalArgumentException("Entity must be of type Grade");
        }

        dbConnAbs.executeTransaction(em -> {
            Grade gradeEntity = (Grade) grade;

            // Fetch the existing Grade from the database
            Grade existingGrade = em.find(Grade.class, gradeEntity.getId());
            if (existingGrade == null || !existingGrade.getActive()) {
                throw new IllegalStateException("Grade must exist and be active to delete.");
            }

            // Soft delete: Mark the grade as inactive
            existingGrade.setActive(false);

            // Persist changes
            em.merge(existingGrade);
        });
    }


    @Override
    public <T extends PersistableEntity> T findByStudentIdAndSubjectId(int studentId, int subjectId) throws Exception {
        String jpql = "SELECT g FROM StudentsSubject g WHERE g.student.id = :studentId AND g.subject.id = :subjectId AND g.active = TRUE";
        return (T) this.dbConnAbs.executeQueryTransaction(entityManager -> {
            try {
                // Define a TypedQuery for StudentsSubject
                TypedQuery<StudentsSubject> query = entityManager.createQuery(jpql, StudentsSubject.class);
                query.setParameter("studentId", studentId);
                query.setParameter("subjectId", subjectId);

                // Retrieve a single result
                return query.getSingleResult();
            } catch (NoResultException e) {
                // Handle the case when no result is found
                return null;
            }
        }, StudentsSubject.class);
    }


    @Override
    public <T extends PersistableEntity> void addGrade(T grade) throws Exception {
        if (!(grade instanceof Grade)) {
            throw new IllegalArgumentException("Entity must be of type Grade");
        }

        dbConnAbs.executeTransaction(em -> {
            Grade gradeEntity = (Grade) grade;

            // Ensure the StudentsSubject relationship exists
            StudentsSubject studentsSubject = gradeEntity.getStudentSubject();
            if (studentsSubject == null || studentsSubject.getId() == null) {
                throw new IllegalArgumentException("StudentsSubject must be provided with a valid ID.");
            }

            em.persist(studentsSubject);
            // Link the Grade to the existing StudentsSubject
            gradeEntity.setStudentSubject(studentsSubject);

            // Set additional fields
            gradeEntity.setAddedDate(new Timestamp(System.currentTimeMillis()));
            gradeEntity.setActive(true);

            // Persist the grade

            em.persist(gradeEntity);
        });
    }


    @Override
    public <T extends PersistableEntity> List<T> findAllStudentsByTeacherId(Class<T> entityType, int teacherId) throws Exception {
        String jpql = "SELECT DISTINCT s.student FROM StudentsSubject s WHERE s.subject.teacher.id = :teacherId";

        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(jpql, entityType);
            typedQuery.setParameter("teacherId", teacherId);
            return typedQuery;
        }, TypedQuery.class);
        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> void addSubject(String subjectName, int teacherId,int[] studentIds) throws Exception {
        try {
            dbConnAbs.executeStoredProcedure("add_or_activate_subject", subjectName, teacherId, studentIds);
            System.out.println("Subject added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to deactivate subject: " + e.getMessage());
        }
    }

    @Override
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        this.dbConnAbs.executeTransaction(entityManager -> entityManager.persist(entity));
    }

    @Override
    public Subject findBySubjectByName(String subjectName) throws Exception {
        // Define the base JPQL query specifically for the Subject entity
        String jpql = "SELECT s FROM Subject s WHERE s.subjectName = :subjectName AND s.active = TRUE";

        // Execute the query transaction
        return this.dbConnAbs.executeQueryTransaction(entityManager -> {
            // Create and configure a TypedQuery for the Subject entity
            TypedQuery<Subject> query = entityManager.createQuery(jpql, Subject.class);
            query.setParameter("subjectName", subjectName);  // Set the parameter for the subject name
            query.setMaxResults(1);  // Ensure only the first matching result is considered
            try {
                // Execute the query and return the single result or null if no result found
                return query.getSingleResult();
            } catch (NoResultException e) {
                // Return null if no Subject is found with the given name
                return null;
            }
        }, Subject.class);  // The executeQueryTransaction method needs to handle and cast the return type
    }

    @Override
    public <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception {

        String abstractQuery = "SELECT e FROM %s e".formatted(entityType.getSimpleName());

        TypedQuery<T> query = this.dbConnAbs
                .executeQueryTransaction(entityManager -> entityManager
                .createQuery(abstractQuery, entityType), TypedQuery.class);

        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> void editSubject(int subjectId, String newSubjectName, int[] studentsId) throws Exception {
        try {
            dbConnAbs.executeStoredProcedure("deactivate_subject_and_related_entities", subjectId, newSubjectName, studentsId);
            System.out.println("Subject edit successfully.");
        } catch (Exception e) {
            System.err.println("Failed to deactivate subject: " + e.getMessage());
        }
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGrades(Class<T> entityType) throws Exception {
        String query = "SELECT g FROM Grade g " +
                "JOIN FETCH g.studentSubject ss " +
                "JOIN FETCH ss.student s " +
                "JOIN FETCH ss.subject sub " +
                "WHERE g.active = TRUE";

        return this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> gradeQuery = entityManager.createQuery(query, entityType);
            gradeQuery.setHint("javax.persistence.cache.storeMode", "REFRESH"); // Fetch fresh data
            return gradeQuery.getResultList();
        }, List.class);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllStudents(Class<T> entityType, String userType) throws Exception {
        // Define the base JPQL query
        String baseQuery = "SELECT u FROM %s u WHERE ".formatted(entityType.getSimpleName());
        String whereClause = "u.userType = :userType";
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            typedQuery.setParameter("userType", userType);
            return typedQuery;
        }, TypedQuery.class);

        // Return the result list
        return query.getResultList();
    }


    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectByStudentId(int studentId) throws Exception {
        String jpql = "SELECT s.subject FROM StudentsSubject s WHERE s.student.id = :studentId AND s.active = TRUE";

        // Execute the query transaction
        return this.dbConnAbs.executeQueryTransaction(entityManager -> {
            // Create and configure a TypedQuery for Subject entities
            TypedQuery<Subject> query = entityManager.createQuery(jpql, Subject.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        }, List.class);
    }

    @Override
    public <T extends PersistableEntity> List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception {

        String baseQuery = "SELECT s FROM %s s WHERE ".formatted(entityType.getSimpleName());
        String whereClause = "s.teacher.id = :teacherId AND s.active = TRUE";
        String finalQuery = baseQuery + whereClause;



        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            entityManager.clear();
            entityManager.flush();
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            typedQuery.setParameter("teacherId", teacherId);
            return typedQuery;
        }, TypedQuery.class);

        return query.getResultList();
    }

    public <T extends PersistableEntity> T findById(Class<T> entityType, int id){
        T entity = this.dbConnAbs.executeQueryTransaction(entityManager ->
                        entityManager.find(entityType, id), entityType);
        return entity;
    }

    public <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery.setMaxResults(1);
        }, TypedQuery.class);

        // Retrieve the single result or return null if no result
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair... params) throws Exception {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction and get the results as a list
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery;
        }, TypedQuery.class);

        // Return the full result list
        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectStudentsBySubjectId(Class<T> entityType, int subjectId) throws Exception {
        return List.of();
    }




    @Override
    public <T extends PersistableEntity> List<T> findAllSubjectStudentsByStudentId(Class<T> entityType, int studentId) throws Exception {
        // Define the base JPQL query
        String baseQuery = "SELECT ss FROM %s ss WHERE ".formatted(entityType.getSimpleName());
        String whereClause = "ss.student.id = :studentId AND ss.active = TRUE";  // Filter by studentId and active status
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            typedQuery.setParameter("studentId", studentId);
            return typedQuery;
        }, TypedQuery.class);

        // Return the result list
        return query.getResultList();
    }



    @Override
    public void close() throws IOException {
        this.dbConnAbs.close();
    }
}
