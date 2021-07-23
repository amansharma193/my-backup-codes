package com.foodbrigade.firechat;

public class Story {
    private String date,time,uid,storyid,url,type,text;
    private long timestamp;
    public Story(String date, String time, long timestamp, String uid, String storyid, String url, String type, String text) {
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.uid = uid;
        this.storyid = storyid;
        this.url = url;
        this.type = type;
        this.text = text;
    }
    public Story(){

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
