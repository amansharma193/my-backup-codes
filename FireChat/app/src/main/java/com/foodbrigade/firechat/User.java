package com.foodbrigade.firechat;

import java.io.Serializable;

public class User implements Serializable {
   private String key,username,status,image,date,time,state,token;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    private boolean check=false;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(String key, String username, String status, String image, String date, String time, String state) {
        this.key = key;
        this.username = username;
        this.status = status;
        this.image = image;
        this.date = date;
        this.time = time;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User(String key, String username, String status, String image) {
        this.key = key;
        this.username = username;
        this.status = status;
        this.image = image;
    }

    public User(String username, String status, String image) {
        this.username = username;
        this.status = status;
        this.image = image;
    }

    public User() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
