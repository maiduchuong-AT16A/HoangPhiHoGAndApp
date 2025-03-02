package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CalendarA {

    @SerializedName("id_calendar")
    @Expose
    private int idCalendar;
    @SerializedName("header_calendar")
    @Expose
    private String headerCalendar;
    @SerializedName("body_calendar")
    @Expose
    private String bodyCalendar;
    @SerializedName("type_calendar")
    @Expose
    private TypeCalendar typeCalendar;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("time_start")
    @Expose
    private String timeStart;
    @SerializedName("time_end")
    @Expose
    private String timeEnd;
    @SerializedName("work_day")
    @Expose
    private int workday;


    public CalendarA() {
    }

    public CalendarA(int idCalendar, String headerCalendar, String bodyCalendar, TypeCalendar typeCalendar, String address, String timeStart, String timeEnd, int workday) {
        this.idCalendar = idCalendar;
        this.headerCalendar = headerCalendar;
        this.bodyCalendar = bodyCalendar;
        this.typeCalendar = typeCalendar;
        this.address = address;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.workday = workday;
    }

    public TypeCalendar getTypeCalendar() {
        return typeCalendar;
    }

    public void setTypeCalendar(TypeCalendar typeCalendar) {
        this.typeCalendar = typeCalendar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIdCalendar() {
        return idCalendar;
    }

    public void setIdCalendar(int idCalendar) {
        this.idCalendar = idCalendar;
    }

    public String getHeaderCalendar() {
        return headerCalendar;
    }

    public void setHeaderCalendar(String headerCalendar) {
        this.headerCalendar = headerCalendar;
    }

    public String getBodyCalendar() {
        return bodyCalendar;
    }

    public void setBodyCalendar(String bodyCalendar) {
        this.bodyCalendar = bodyCalendar;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getWorkday() {
        return workday;
    }

    public void setWorkday(int workday) {
        this.workday = workday;
    }
}