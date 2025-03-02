package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.CheckIn;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CheckInResponse {

    @SerializedName("data")
    @Expose
    private CheckIn checkIn;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public CheckInResponse() {
    }

    public CheckInResponse(CheckIn checkIn, String message, int code) {
        this.checkIn = checkIn;
        this.message = message;
        this.code = code;
    }

    public CheckIn getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(CheckIn checkIn) {
        this.checkIn = checkIn;
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
