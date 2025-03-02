package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StaffFeedBack {
    @SerializedName("id_feedback")
    @Expose
    private int idFeedback;
    @SerializedName("time_feedback")
    @Expose
    private String timeFeedback;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("is_read")
    @Expose
    private String isRead;
    @SerializedName("user")
    @Expose
    private User user;

    public StaffFeedBack() {
    }

    public StaffFeedBack(int idFeedback, String timeFeedback, String content, String isRead, User user) {
        this.idFeedback = idFeedback;
        this.timeFeedback = timeFeedback;
        this.content = content;
        this.isRead = isRead;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIdFeedback() {
        return idFeedback;
    }

    public void setIdFeedback(int idFeedback) {
        this.idFeedback = idFeedback;
    }

    public String getTimeFeedback() {
        return timeFeedback;
    }

    public void setTimeFeedback(String timeFeedback) {
        this.timeFeedback = timeFeedback;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}