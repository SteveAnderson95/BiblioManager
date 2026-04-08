package com.bibliomanager;

import com.bibliomanager.model.Librarian;

public class UserSession {

    private static UserSession instance;
    private Librarian currentUser;

    private UserSession(Librarian currentUser) {
        this.currentUser = currentUser;
    }

    public static void start(Librarian librarian) {
        instance = new UserSession(librarian);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clean() {
        instance = null;
    }

    public static boolean isLoggedIn() {
        return instance != null;
    }

    // ACCESSORS

    public Librarian getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Librarian currentUser) {
        this.currentUser = currentUser;
    }
}
