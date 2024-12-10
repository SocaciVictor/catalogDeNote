package com.example.demo.persistence.dao;

import com.example.demo.persistence.connection.Connection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.entities.PersistableEntity;

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
}
