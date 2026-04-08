package com.bibliomanager.model;

public class Category {

    private final long id;
    private String name;
    private String description;

    public Category(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
    }

    // GETTERS

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
