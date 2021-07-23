package com.e.skychat.beans;

public class ChatRequest {
    private String request_type;

    public ChatRequest(String request_type) {
        this.request_type = request_type;
    }

    public ChatRequest() {
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}

