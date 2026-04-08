package com.bibliomanager.model;

public class Book {

    private final long id;
    private String title;
    private String author;
    private String isbn;
    private Category category;
    private String coverImage;
    private int totalQuantity;
    private int availableQuantity;

    public Book (long id, String title, String author, String isbn, Category category, String coverImage, int totalQuantity, int availableQuantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.coverImage = coverImage;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Book (String title, String author, String isbn, Category category, String coverImage, int totalQuantity, int availableQuantity) {
        this.id = 0;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.coverImage = coverImage;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    //GETTERS

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    //SETTERS


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //METHODS

    public boolean isAvailable () {
        return availableQuantity > 0;
    }

    public void incrementAvailability () {
        if (availableQuantity < totalQuantity)
            availableQuantity ++;
    }

    public void decrementAvailability () {
        if (availableQuantity > 0)
            availableQuantity --;
    }
}
