package com.e.skychat.beans;

public class Message {
    private String date;
    private String time;
    private long timestamp;
    private String from;
    private String to;
    private String messageId;
    private String message;
    private String type;
    private String senderIcon;

    public String getSenderIcon() {
        return senderIcon;
    }

    public void setSenderIcon(String senderIcon) {
        this.senderIcon = senderIcon;
    }

    public Message(String date, String time, long timestamp, String from, String to, String messageId, String message, String type) {
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.message = message;
        this.type = type;
    }

    public Message() {
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
}
