package com.example.onetap.model;

public class News {
    private final String title;
    private final String description;
    private final String category;
    private final String author;
    private final String publishDate;
    private final int imageResId;

    public News(String title, String description, String category, String author, String publishDate, int imageResId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.publishDate = publishDate;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public int getImageResId() {
        return imageResId;
    }
}