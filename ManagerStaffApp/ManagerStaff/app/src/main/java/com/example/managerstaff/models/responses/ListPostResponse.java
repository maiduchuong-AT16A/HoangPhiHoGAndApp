package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Post;
import com.example.managerstaff.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListPostResponse {

    @SerializedName("data")
    @Expose
    private List<Post> listPosts;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListPostResponse() {
    }

    public ListPostResponse(List<Post> listPosts, String message, int code) {
        this.listPosts = listPosts;
        this.message = message;
        this.code = code;
    }

    public List<Post> getListPosts() {
        return listPosts;
    }

    public void setListPosts(List<Post> listPosts) {
        this.listPosts = listPosts;
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
