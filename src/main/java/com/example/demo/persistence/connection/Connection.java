package com.example.demo.persistence.connection;

import com.example.demo.persistence.entities.PersistableEntity;

import java.io.Closeable;
import java.util.List;

public abstract class Connection implements Closeable {
    public abstract <T extends PersistableEntity> void save(T entity) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    /* TODO: Add Update & Delete */
}
