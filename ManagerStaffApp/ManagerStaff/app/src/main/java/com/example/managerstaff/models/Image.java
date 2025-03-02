package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Image {
    @SerializedName("id_image")
    @Expose
    private int idImage;
    @SerializedName("image")
    @Expose
    private String image;

    public Image() {
    }

    public Image(int idImage, String image) {
        this.idImage = idImage;
        this.image = image;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
