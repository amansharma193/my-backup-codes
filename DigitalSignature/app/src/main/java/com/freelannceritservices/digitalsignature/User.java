package com.freelannceritservices.digitalsignature;

public class User {
    private String name, id, password;

    public User(String name, String id, String mobile, String password, String email, String status) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
