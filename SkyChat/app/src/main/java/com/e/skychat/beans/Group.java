package com.e.skychat.beans;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupId;
    private String groupName;
    private String icon;
    private String createdAt;
    private String createdBy;
    private String description;

    public Group(String groupId, String groupName, String icon, String createdAt, String createdBy, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.icon = icon;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.description = description;
    }

    public Group() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
