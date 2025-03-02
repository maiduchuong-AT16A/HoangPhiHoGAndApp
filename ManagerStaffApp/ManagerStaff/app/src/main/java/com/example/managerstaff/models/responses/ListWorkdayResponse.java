package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.NotificationData;
import com.example.managerstaff.models.Workday;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListWorkdayResponse {

    @SerializedName("data")
    @Expose
    private List<Workday> listWorkdays;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListWorkdayResponse() {
    }

    public ListWorkdayResponse(List<Workday> listWorkdays, String message, int code) {
        this.listWorkdays = listWorkdays;
        this.message = message;
        this.code = code;
    }

    public List<Workday> getListWorkdays() {
        return listWorkdays;
    }

    public void setListWorkdays(List<Workday> listWorkdays) {
        this.listWorkdays = listWorkdays;
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
