package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.TypePost;
import com.example.managerstaff.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListTypePostResponse {
    @SerializedName("data")
    @Expose
    private List<TypePost> listTypePosts;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListTypePostResponse() {
    }

    public ListTypePostResponse(List<TypePost> listTypePosts, String message, int code) {
        this.listTypePosts = listTypePosts;
        this.message = message;
        this.code = code;
    }

    public List<TypePost> getListTypePosts() {
        return listTypePosts;
    }

    public void setListTypePosts(List<TypePost> listTypePosts) {
        this.listTypePosts = listTypePosts;
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
