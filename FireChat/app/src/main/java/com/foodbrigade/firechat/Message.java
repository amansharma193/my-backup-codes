package com.foodbrigade.firechat;

public class Message {
    private String message,type,date,time,from,key,to,sendericon;
    private long timestamp;
    public Message(String message, String type, String date, String time, String from,String to,String key,long timestamp) {
        this.message = message;
        this.type = type;
        this.date = date;
        this.time = time;
        this.from = from;
        this.key=key;
        this.to=to;
        this.timestamp=timestamp;
    }

    public String getSendericon() {
        return sendericon;
    }

    public void setSendericon(String sendericon) {
        this.sendericon = sendericon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Message() {
    }
}
