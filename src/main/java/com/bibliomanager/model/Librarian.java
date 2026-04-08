package com.bibliomanager.model;

public class Librarian extends Person {

    private String userName;
    private String password;

    public Librarian(long id, String firstName, String lastName, String userName, String email, String password) {
        super(id, firstName, lastName, email);
        this.userName = userName;
        this.password = password;
    }

    public Librarian(String firstName, String lastName, String userName, String email, String password) {
        super(firstName, lastName, email);
        this.userName = userName;
        this.password = password;
    }

    // GETTERS

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    // SETTERS

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
