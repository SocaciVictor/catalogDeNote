package com.example.demo.persistence.connection.database;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.PersistableEntity;
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
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        /* Before Abstraction */
        //EntityTransaction transaction = entityManager.getTransaction();
        //transaction.begin();
        //entityManager.persist(entity);
        //transaction.commit();
        this.dbConnAbs.executeTransaction(entityManager -> entityManager.persist(entity));
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

    public <T extends PersistableEntity> T findById(Class<T> entityType, Long id){
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
    public void close() throws IOException {
        this.dbConnAbs.close();
    }
}
