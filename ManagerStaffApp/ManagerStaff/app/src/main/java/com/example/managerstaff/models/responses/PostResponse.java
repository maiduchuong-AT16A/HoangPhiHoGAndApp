package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PostResponse {

    @SerializedName("data")
    @Expose
    private Post post;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public PostResponse() {
    }

    public PostResponse(Post post, String message, int code) {
        this.post = post;
        this.message = message;
        this.code = code;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
