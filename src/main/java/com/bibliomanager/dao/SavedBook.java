package com.bibliomanager.dao;

import java.sql.Date;

public class SavedBook {

    private String title;
    private String author;
    private String genre;
    private Date date;
    private String image;

    public SavedBook (String title, String author, String genre, Date date, String image) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.date = date;
        this.image = image;
    }

    public String getTitle () {
        return title;
    }
    public String getAuthor () {
        return author;
    }
    public String getGenre () {
        return genre;
    }
    public Date getDate () {
        return date;
    }
    public String getImage () {
        return image;
    }
}
