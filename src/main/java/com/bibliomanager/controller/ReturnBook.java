package com.bibliomanager.controller;

import java.util.Date;

public class ReturnBook {
    public final String title;
    public final String author;
    public final String genre;
    public final Date date;
    public final String image;

    public ReturnBook (String title, String author, String genre, Date date, String image) {
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
