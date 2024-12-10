package com.example.demo.persistence.connection;

public class ParameterPair<T,W> {
    private T name; //email
    private W value; //mock@mock.com

    public ParameterPair(String name, String value) {
        this.name = (T) name;
        this.value = (W) value;
    }

    public T getName() {
        return name;
    }

    public void setName(T name) {
        this.name = name;
    }

    public W getValue() {
        return value;
    }

    public void setValue(W value) {
        this.value = value;
    }
}
