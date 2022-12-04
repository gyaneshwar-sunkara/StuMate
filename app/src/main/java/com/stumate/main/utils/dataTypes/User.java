package com.stumate.main.utils.dataTypes;

public class User {

    private String uid;
    private String displayName;
    private String imageUrl;
    private String className;

    public User(String uid, String displayName, String imageUrl, String className) {
        this.uid = uid;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
