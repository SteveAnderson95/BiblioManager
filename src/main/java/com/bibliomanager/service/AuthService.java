package com.bibliomanager.service;

import com.bibliomanager.UserSession;
import com.bibliomanager.model.Librarian;
import com.bibliomanager.repository.LibrarianRepository;

public class AuthService {

    public boolean login (String username, String password) {
        LibrarianRepository repository = new LibrarianRepository();
        Librarian librarian = repository.findByUsername(username);

        if (librarian != null && librarian.getPassword().equals(password)) {
            UserSession.start(librarian);
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        UserSession.clean();
    }
}
