package com.example.demo.persistence.connection.database;

import jakarta.persistence.*;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;


public class DatabaseConnectionAbstract {
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseConnectionAbstract(String persistenceUnit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void close() throws IOException {
        entityManager.close();
        entityManagerFactory.close();
    }

    public void executeTransaction(Consumer<EntityManager> action) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Transaction error: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    public <T,R> R executeQueryTransaction(Function<EntityManager, T> action, Class<R> result) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Object queryResult = null;

        try {
            entityTransaction.begin();
            queryResult = action.apply(entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getLocalizedMessage());
            entityTransaction.rollback();
        }

        return (R) queryResult;
    }

    public void executeStoredProcedure(String procedureName, Object... params) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Construct the CALL query dynamically based on the procedure name and parameters
            String sqlCall = String.format("CALL %s(", procedureName);
            sqlCall += String.join(", ", Collections.nCopies(params.length, "?")) + ")";
            Query query = em.createNativeQuery(sqlCall);

            // Set parameters
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }

            // Execute the CALL statement
            query.executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error executing stored procedure: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
