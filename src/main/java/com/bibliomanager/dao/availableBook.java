package com.bibliomanager.dao;

import java.sql.Date;

public class availableBook {

    private final String title;
    private final String author;
    private final String genre;
    private final String image;
    private final Date publishedDate;

    public availableBook (String title, String author, String genre, String image, Date publishedDate) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.image = image;
        this.publishedDate = publishedDate;
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
    public String getImage () {
        return image;
    }
    public Date getPublishedDate () {
        return publishedDate;
    }
}
