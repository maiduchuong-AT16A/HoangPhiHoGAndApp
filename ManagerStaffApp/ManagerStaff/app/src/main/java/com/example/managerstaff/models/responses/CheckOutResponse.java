package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.CheckIn;
import com.example.managerstaff.models.CheckOut;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CheckOutResponse {

    @SerializedName("data")
    @Expose
    private CheckOut checkOut;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public CheckOutResponse() {
    }

    public CheckOutResponse(CheckOut checkOut, String message, int code) {
        this.checkOut = checkOut;
        this.message = message;
        this.code = code;
    }

    public CheckOut getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(CheckOut checkOut) {
        this.checkOut = checkOut;
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
