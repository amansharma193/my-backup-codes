package com.transportervendor.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("timestamp")
    @Expose
    private long timestamp;

    public Rating(String rating, String feedback, String userName, String imageUrl, String userId, long timestamp) {
        this.rating = rating;
        this.feedback = feedback;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Rating() {
    }

    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getFeedback() {
        return feedback;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
