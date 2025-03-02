package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.CalendarA;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListCalendarResponse {

    @SerializedName("data")
    @Expose
    private List<CalendarA> listCalendarAS;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListCalendarResponse() {
    }

    public ListCalendarResponse(List<CalendarA> listCalendarAS, String message, int code) {
        this.listCalendarAS = listCalendarAS;
        this.message = message;
        this.code = code;
    }

    public List<CalendarA> getListCalendars() {
        return listCalendarAS;
    }

    public void setListCalendars(List<CalendarA> listCalendarAS) {
        this.listCalendarAS = listCalendarAS;
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
