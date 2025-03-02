package com.example.managerstaff.models.responses;

import com.example.managerstaff.models.TypeCalendar;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListTypeCalendarResponse {

    @SerializedName("data")
    @Expose
    private List<TypeCalendar> listTypeCalendars;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private int code;

    public ListTypeCalendarResponse() {
    }

    public ListTypeCalendarResponse(List<TypeCalendar> listTypeCalendars, String message, int code) {
        this.listTypeCalendars = listTypeCalendars;
        this.message = message;
        this.code = code;
    }

    public List<TypeCalendar> getListTypeCalendars() {
        return listTypeCalendars;
    }

    public void setListTypeCalendars(List<TypeCalendar> listTypeCalendars) {
        this.listTypeCalendars = listTypeCalendars;
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
