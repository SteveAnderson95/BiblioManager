package com.bibliomanager.service;

import com.bibliomanager.model.Book;
import com.bibliomanager.repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookService {

    private final BookRepository bookRepo = new BookRepository();

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public List<Book> searchBooks(String query, Long id, String status) {
        return bookRepo.search(query, id, status);
    }

    public void addBook(Book book) {
        validate(book);
        try {
            bookRepo.insert(book);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new RuntimeException("ISBN already exists.");
            throw new RuntimeException("Error adding book", e);
        }
    }

    public void updateBook(Book book) {
        validate(book);
        bookRepo.update(book);
    }

    public void deleteBook(long id) {
        try {
            bookRepo.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("Cannot delete: book has active loans.");
            throw new RuntimeException("Error deleting book", e);
        }
    }

    private void validate(Book book) {
        if(book.getTitle() == null || book.getTitle().isBlank())
            throw new IllegalArgumentException("Title is required.");
        if (book.getAuthor() == null || book.getAuthor().isBlank())
            throw new IllegalArgumentException("Author is required.");
        if(book.getIsbn() == null || book.getIsbn().isBlank())
            throw new IllegalArgumentException("ISBN is required.");
        if(book.getCategory() == null)
            throw new IllegalArgumentException("Category is required.");
        if (book.getTotalQuantity() < 1)
            throw new IllegalArgumentException("Total quantity must be >= 1");
        if (book.getAvailableQuantity() < 0)
            throw new IllegalArgumentException("Available quantity cannot be negative.");
        if (book.getAvailableQuantity() > book.getTotalQuantity())
            throw new IllegalArgumentException("Available cannot exceed total");
    }
}
