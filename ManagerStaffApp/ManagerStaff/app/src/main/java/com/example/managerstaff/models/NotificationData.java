package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NotificationData {
    @SerializedName("id_notification")
    @Expose
    private int idNotify;
    @SerializedName("title_notification")
    @Expose
    private String title;
    @SerializedName("body_notification")
    @Expose
    private String body;
    @SerializedName("time_notification")
    @Expose
    private String time;

    @SerializedName("is_read")
    @Expose
    private int isRead;

    @SerializedName("type_notification")
    @Expose
    private int typeNotify;

    @SerializedName("user")
    @Expose
    private int idUser;

    @SerializedName("id_data")
    @Expose
    private int idData;

    public NotificationData() {
    }

    public NotificationData(int idNotify, String title, String body, String time, int isRead, int typeNotify, int idUser, int idData) {
        this.idNotify = idNotify;
        this.title = title;
        this.body = body;
        this.time = time;
        this.isRead = isRead;
        this.typeNotify = typeNotify;
        this.idUser = idUser;
        this.idData = idData;
    }

    public int getIdNotify() {
        return idNotify;
    }

    public void setIdNotify(int idNotify) {
        this.idNotify = idNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getTypeNotify() {
        return typeNotify;
    }

    public void setTypeNotify(int typeNotify) {
        this.typeNotify = typeNotify;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdData() {
        return idData;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }
}
