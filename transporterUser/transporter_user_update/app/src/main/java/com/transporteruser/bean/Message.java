package com.transporteruser.bean;

public class Message {
    private String messageId;
    private String from,to,message;
    private long timeStamp;

    public Message() {
    }

    public Message(String messageId, String from, String to, String message, long timeStamp) {
        this.messageId = messageId;
        this.from = from;
        this.to = to;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
