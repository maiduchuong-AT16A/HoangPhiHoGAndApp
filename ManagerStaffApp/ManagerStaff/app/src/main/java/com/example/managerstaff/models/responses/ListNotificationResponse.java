package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.NotificationData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListNotificationResponse {

    @SerializedName("data")
    @Expose
    private List<NotificationData> listNotificationPosts;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListNotificationResponse() {
    }

    public ListNotificationResponse(List<NotificationData> listNotificationPosts, String message, int code) {
        this.listNotificationPosts = listNotificationPosts;
        this.message = message;
        this.code = code;
    }

    public List<NotificationData> getListNotificationPosts() {
        return listNotificationPosts;
    }

    public void setListNotificationPosts(List<NotificationData> listNotificationPosts) {
        this.listNotificationPosts = listNotificationPosts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
