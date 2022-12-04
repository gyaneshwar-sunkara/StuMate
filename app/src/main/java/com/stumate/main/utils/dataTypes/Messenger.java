package com.stumate.main.utils.dataTypes;

public class Messenger {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String displayName;
    private String imageUrl;
    private String lastMessage;
    private String totalUnread;
    private String timestamp;

    public Messenger() {
    }

    public Messenger(String uid, String displayName, String imageUrl, String totalUnread, String lastMessage, String timestamp) {
        this.uid = uid;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.totalUnread = totalUnread;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // TODO: get to know wait and notify;


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

    public String getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(String totalUnread) {
        this.totalUnread = totalUnread;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
