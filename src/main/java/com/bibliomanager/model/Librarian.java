package com.bibliomanager.model;

public class Librarian extends Person {

    private String username;
    private String password;

    public Librarian(long id, String firstName, String lastName, String username, String email, String password) {
        super(id, firstName, lastName, email);
        this.username = username;
        this.password = password;
    }

    public Librarian(String firstName, String lastName, String username, String email, String password) {
        super(firstName, lastName, email);
        this.username = username;
        this.password = password;
    }

    // GETTERS

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // SETTERS

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
