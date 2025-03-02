package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.StaffFeedBack;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StaffFeedBackResponse {

    @SerializedName("data")
    @Expose
    private StaffFeedBack feedBack;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public StaffFeedBackResponse() {
    }

    public StaffFeedBackResponse(StaffFeedBack feedBack, String message, int code) {
        this.feedBack = feedBack;
        this.message = message;
        this.code = code;
    }

    public StaffFeedBack getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(StaffFeedBack feedBack) {
        this.feedBack = feedBack;
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
