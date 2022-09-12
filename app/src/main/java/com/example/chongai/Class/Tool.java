package com.example.chongai.Class;

public class Tool {
    private int imageId;

    private String title;

    public Tool(String title,int imageId) {
        this.imageId = imageId;
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }
}
