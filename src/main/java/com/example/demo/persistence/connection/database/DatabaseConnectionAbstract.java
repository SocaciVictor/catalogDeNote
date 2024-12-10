package com.example.demo.persistence.connection.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.io.IOException;
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
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            action.accept(entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getLocalizedMessage());
            entityTransaction.rollback();
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

    public EntityManager getEntityManager() {
        return entityManager;
    }
}