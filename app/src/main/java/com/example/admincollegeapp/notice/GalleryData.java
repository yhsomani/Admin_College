package com.example.admincollegeapp.notice;

public class GalleryData {
    private String imageUrl;
    private String category;
    private String key; // Added key for deletion

    public GalleryData() {
        // Default constructor required for Firebase
    }

    public GalleryData(String imageUrl, String category, String key) {
        this.imageUrl = imageUrl;
        this.category = category;
        this.key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}