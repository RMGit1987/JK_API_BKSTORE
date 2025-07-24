package com.bkstore.fastapi.api.payloads;

public class Book {
    private Integer id;
    private String name; 
    private String author;
    private Integer published_year; 
    private String book_summary; 

    public Book() {
    }

    // Constructor for creating new books (without ID) for POST requests
    public Book(String name, String author, Integer published_year, String book_summary) {
        this.name = name;
        this.author = author;
        this.published_year = published_year;
        this.book_summary = book_summary;
    }

    // Full constructor (e.g., for deserialization or PUT if ID is in payload)
    public Book(Integer id, String name, String author, Integer published_year, String book_summary) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.published_year = published_year;
        this.book_summary = book_summary;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { 
        return name;
    }

    public void setName(String name) { 
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublished_year() {
        return published_year;
    }

    public void setPublished_year(Integer published_year) { 
        this.published_year = published_year;
    }

    public String getBook_summary() {
        return book_summary;
    }

    public void setBook_summary(String book_summary) { 
        this.book_summary = book_summary;
    }
}