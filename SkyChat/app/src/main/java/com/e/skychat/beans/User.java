package com.e.skychat.beans;

import java.io.Serializable;

public class User implements Serializable {
  private String uid;
  private String name;
  private String status;
  private String image;
  private String date;
  private String time;
  private String state;
  private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public User(String uid, String name, String status, String image, String date, String time, String state) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.image = image;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public User(String uid, String name, String status, String image) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public User() {
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
