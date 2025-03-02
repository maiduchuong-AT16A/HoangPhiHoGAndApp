package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PartResponse {

    @SerializedName("data")
    @Expose
    private Part part;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public PartResponse() {
    }

    public PartResponse(Part part, String message, int code) {
        this.part = part;
        this.message = message;
        this.code = code;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
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
