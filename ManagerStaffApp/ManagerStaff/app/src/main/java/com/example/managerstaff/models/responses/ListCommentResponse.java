package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Comment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListCommentResponse {

    @SerializedName("data")
    @Expose
    private List<Comment> listComments;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListCommentResponse() {
    }

    public ListCommentResponse(List<Comment> listComments, String message, int code) {
        this.listComments = listComments;
        this.message = message;
        this.code = code;
    }

    public List<Comment> getListComments() {
        return listComments;
    }

    public void setListComments(List<Comment> listComments) {
        this.listComments = listComments;
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
