package com.example.onetap.model;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String link;
    private String category;
    private String title;
    private String pubDate;
    private String description;
    private String thumbnail;

    // Constructor (opsional)
    public NewsItem(String link, String title, String pubDate, String description, String thumbnail) {
        this.link = link;
        this.title = title;
        this.pubDate = pubDate;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    // Getters
    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
