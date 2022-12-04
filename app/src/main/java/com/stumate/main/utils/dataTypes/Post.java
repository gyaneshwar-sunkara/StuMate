package com.stumate.main.utils.dataTypes;

import com.google.firebase.Timestamp;

import java.util.List;

public class Post {
    private String pid;
    private String uid;
    private String displayName;
    private String imageUrl;
    private String postUrl;
    private String caption;
    private int inks;
    private String tag;
    private Timestamp timestamp;
    private List<String> saved;


    public String getPid() {
        return pid;
    }


    public Post(String uid, String displayName, String imageUrl, String postUrl, String caption, int inks, String tag, Timestamp timestamp, List<String> saved) {
        this.uid = uid;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.postUrl = postUrl;
        this.caption = caption;
        this.inks = inks;
        this.tag = tag;
        this.timestamp = timestamp;
        this.saved = saved;
    }

    public Post(String pid, Post post, List<String> saved) {
        this.pid = pid;
        this.uid = post.getUid();
        this.displayName = post.getDisplayName();
        this.imageUrl = post.getImageUrl();
        this.postUrl = post.getPostUrl();
        this.caption = post.getCaption();
        this.inks = post.getInks();
        this.tag = post.getTag();
        this.timestamp = post.getTimestamp();
        this.saved = saved;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getInks() {
        return inks;
    }

    public void setInks(int inks) {
        this.inks = inks;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Post() {
        //empty constructor needed
    }

    public List<String> getSaved() {
        return saved;
    }

    public void setSaved(List<String> saved) {
        this.saved = saved;
    }
}


