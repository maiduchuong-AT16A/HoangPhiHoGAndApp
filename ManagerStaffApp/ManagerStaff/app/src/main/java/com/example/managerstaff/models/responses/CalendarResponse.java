package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.CalendarA;
import com.example.managerstaff.models.Part;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CalendarResponse {

    @SerializedName("data")
    @Expose
    private CalendarA calendar;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public CalendarResponse() {
    }

    public CalendarResponse(CalendarA calendar, String message, int code) {
        this.calendar = calendar;
        this.message = message;
        this.code = code;
    }

    public CalendarA getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarA calendar) {
        this.calendar = calendar;
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
