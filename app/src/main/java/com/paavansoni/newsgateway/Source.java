package com.paavansoni.newsgateway;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Source implements Serializable {
    private String id;
    private String name;
    private String category;

    Source(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
