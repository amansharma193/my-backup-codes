package com.transportervendor.beans;

import java.io.Serializable;

public class Message implements Serializable,Comparable {
    private String message,date,time,from,id,to;
    private long timeStamp;

    public Message(String message, String date, String time, String from, String id, String to, long timeStamp) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.from = from;
        this.id = id;
        this.to = to;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Message() {
    }

    @Override
    public int compareTo(Object o) {
        Message msg=(Message)o;
        return (int)(this.timeStamp-msg.timeStamp);
    }
}
