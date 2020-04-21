package com.paavansoni.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {
    private String author;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private String publishedAt;

    public Article(String author, String title, String description, String url, String imageUrl, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishedAt = publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPublishedAt() {
        return publishedAt;
    }
}
