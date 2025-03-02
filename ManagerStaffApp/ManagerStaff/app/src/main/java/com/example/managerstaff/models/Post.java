package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Post {
    @SerializedName("id_post")
    @Expose
    private int idPost;
    @SerializedName("type_post")
    @Expose
    private TypePost typePost;
    @SerializedName("header_post")
    @Expose
    private String headerPost;
    @SerializedName("time_post")
    @Expose
    private String timePost;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("num_like")
    @Expose
    private String numLike;
    @SerializedName("num_comment")
    @Expose
    private String numComment;
    @SerializedName("images")
    @Expose
    private ArrayList<Image> listImages;
    @SerializedName("comments")
    @Expose
    private ArrayList<Comment> listComments;
    @SerializedName("user")
    @Expose
    private User user;

    public Post() {
    }

    public Post(int idPost, TypePost typePost, String headerPost, String timePost, String content, String numLike, String numComment, ArrayList<Image> listImages, ArrayList<Comment> listComments, User user) {
        this.idPost = idPost;
        this.typePost = typePost;
        this.headerPost = headerPost;
        this.timePost = timePost;
        this.content = content;
        this.numLike = numLike;
        this.numComment = numComment;
        this.listImages = listImages;
        this.listComments = listComments;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypePost getTypePost() {
        return typePost;
    }

    public void setTypePost(TypePost typePost) {
        this.typePost = typePost;
    }

    public String getHeaderPost() {
        return headerPost;
    }

    public void setHeaderPost(String headerPost) {
        this.headerPost = headerPost;
    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumLike() {
        return numLike;
    }

    public void setNumLike(String numLike) {
        this.numLike = numLike;
    }

    public String getNumComment() {
        return numComment;
    }

    public void setNumComment(String numComment) {
        this.numComment = numComment;
    }

    public ArrayList<Image> getListImages() {
        return listImages;
    }

    public void setListImages(ArrayList<Image> listImages) {
        this.listImages = listImages;
    }

    public ArrayList<Comment> getListComments() {
        return listComments;
    }

    public void setListComments(ArrayList<Comment> listComments) {
        this.listComments = listComments;
    }
}
