package com.foodbrigade.firechat;

import java.io.Serializable;

public class Group implements Serializable {
    private String createdBy,createdAt,icon,description,name,groupid;

    public Group(String createdBy, String createdAt, String icon, String description, String name, String groupid) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.icon = icon;
        this.description = description;
        this.name = name;
        this.groupid = groupid;
    }

    public Group() {
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
