package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.Part;
import com.example.managerstaff.models.Workday;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class WorkdayResponse {

    @SerializedName("data")
    @Expose
    private Workday workday;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public WorkdayResponse() {
    }

    public WorkdayResponse(Workday workday, String message, int code) {
        this.workday = workday;
        this.message = message;
        this.code = code;
    }

    public Workday getWorkday() {
        return workday;
    }

    public void setWorkday(Workday workday) {
        this.workday = workday;
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
