package com.example.managerstaff.models.responses;


import com.example.managerstaff.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListUserResponse {
    @SerializedName("data")
    @Expose
    private List<User> listUsers;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListUserResponse() {
    }

    public ListUserResponse(List<User> listUsers, String message, int code) {
        this.listUsers = listUsers;
        this.message = message;
        this.code = code;
    }

    public List<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(List<User> listUsers) {
        this.listUsers = listUsers;
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