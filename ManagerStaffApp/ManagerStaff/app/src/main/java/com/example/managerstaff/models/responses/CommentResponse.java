package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Comment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CommentResponse {

    @SerializedName("data")
    @Expose
    private Comment comment;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public CommentResponse() {
    }

    public CommentResponse(Comment comment, String message, int code) {
        this.comment = comment;
        this.message = message;
        this.code = code;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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
