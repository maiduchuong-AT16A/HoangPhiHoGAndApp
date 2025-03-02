package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Part;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListPartResponse {

    @SerializedName("data")
    @Expose
    private List<Part> listParts;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListPartResponse() {
    }

    public ListPartResponse(List<Part> listParts, String message, int code) {
        this.listParts = listParts;
        this.message = message;
        this.code = code;
    }

    public List<Part> getListParts() {
        return listParts;
    }

    public void setListParts(List<Part> listParts) {
        this.listParts = listParts;
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
