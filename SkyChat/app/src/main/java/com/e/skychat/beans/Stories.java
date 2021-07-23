package com.e.skychat.beans;

import java.io.Serializable;

public class Stories implements Serializable {
    private String uid;
    private String storyId;
    private String date;
    private String time;
    private long timestamp;
    private String imageUrl;
    private String type;
    private String text;

    public Stories() {
    }

    public Stories(String uid, String storyId, String date, String time, long timestamp, String imageUrl, String type, String text) {
        this.uid = uid;
        this.storyId = storyId;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.type = type;
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
