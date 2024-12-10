package com.example.demo.persistence.connection.database;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.*;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseConnection extends Connection {
    //private EntityManager entityManager;
    //private EntityManagerFactory entityManagerFactory;
    private DatabaseConnectionAbstract dbConnAbs;

    public DatabaseConnection(String persistenceUnit) {
        //entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        //entityManager = entityManagerFactory.createEntityManager();
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
    public <T extends PersistableEntity> void delete(T entity) throws Exception {
        this.dbConnAbs.executeTransaction(entityManager -> {
            if (entity instanceof Subject) {
                Subject subject = (Subject) entity;
                // Deactivate all linked StudentsSubjects entries
                try {
                    findAllSubjectStudentsBySubjectId(StudentsSubject.class, ((Subject) entity).getTeacher().getId()).forEach(ss -> {
                         ss.setActive(false);
                         entityManager.merge(ss);
                        try {
                            List<Grade> gradesList = findAllGradesByStudentId(Grade.class, ((Subject) entity).getTeacher().getId());
                            for (Grade grade : gradesList)
                            {
                                grade.setActive(false);
                                entityManager.merge(grade);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                     });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // Now deactivate the subject itself
                subject.setActive(false);
                entityManager.merge(subject);
            } else if (entity instanceof Grade) {
                Grade grade = (Grade) entity;
            } else {
                throw new IllegalArgumentException("Unsupported entity type for delete operation");
            }
        });
    }


    @Override
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        /* Before Abstraction */
        //EntityTransaction transaction = entityManager.getTransaction();
        //transaction.begin();
        //entityManager.persist(entity);
        //transaction.commit();
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
        /* Before Abstraction */
        //List<T> result = new ArrayList<>();
        //EntityTransaction transaction = entityManager.getTransaction();
        //String abstractQuery = "SELECT e FROM %s e".formatted(entityType.getSimpleName());
        //try {
        //    transaction.begin();
        //    TypedQuery<T> query = entityManager
        //            .createQuery(abstractQuery, entityType);
        //    result = query.getResultList();
        //    transaction.commit();
        //} catch (RuntimeException e) {
        //    System.err.println("Transaction error: " + e.getLocalizedMessage());
        //    /* revert the transaction */
        //    transaction.rollback();
        //}
        //return result;

        String abstractQuery = "SELECT e FROM %s e".formatted(entityType.getSimpleName());

        TypedQuery<T> query = this.dbConnAbs
                .executeQueryTransaction(entityManager -> entityManager
                .createQuery(abstractQuery, entityType), TypedQuery.class);

        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllGrades(Class<T> entityType) throws Exception {
        String query = "SELECT g FROM Grade g " +
                "JOIN FETCH g.studentSubject ss " +
                "JOIN FETCH ss.student s " +
                "JOIN FETCH ss.subject sub " +
                "WHERE g.active = TRUE";

        TypedQuery<T> gradeQuery = this.dbConnAbs.executeQueryTransaction(entityManager ->
                entityManager.createQuery(query, entityType), TypedQuery.class);

        return gradeQuery.getResultList();
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
    public <T extends PersistableEntity> List<T> findTeacherCoursesById(Class<T> entityType, int teacherId) throws Exception {

        String baseQuery = "SELECT s FROM %s s WHERE ".formatted(entityType.getSimpleName());
        String whereClause = "s.teacher.id = :teacherId AND s.active = TRUE";
        String finalQuery = baseQuery + whereClause;

        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
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

    public <T extends PersistableEntity> List<T> findAllSubjectStudentsBySubjectId(EntityManager entityManager, Class<T> entityType, int subjectId) throws Exception {
        String baseQuery = "SELECT ss FROM %s ss WHERE ".formatted(entityType.getSimpleName());
        String whereClause = "ss.subject.id = :subjectId AND ss.active = TRUE";
        String finalQuery = baseQuery + whereClause;
        TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
        typedQuery.setParameter("subjectId", subjectId);
        return typedQuery.getResultList();
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
