package com.stumate.main.utils.dataTypes;


public class Message {
    private String uid;
    private String message;
    private String imageUrl;
    private String timestamp;


    public Message() {
    }

    public Message(String uid, String message, String timestamp) {
        this.uid = uid;
        this.message = message;
        this.imageUrl = null;
        this.timestamp = timestamp;
    }

    public Message(String uid, String message, String imageUrl, String timestamp) {
        this.uid = uid;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setString(String timestamp) {
        this.timestamp = timestamp;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
