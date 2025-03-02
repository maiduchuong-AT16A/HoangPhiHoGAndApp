package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StatisticalTimeKeeping {
    @SerializedName("id_statistical")
    @Expose
    private int idStatistical;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("num_day_off")
    @Expose
    private String numDayOff;
    @SerializedName("num_workday")
    @Expose
    private String numWorkday;
    @SerializedName("num_late_day")
    @Expose
    private String numLateDay;
    @SerializedName("num_leave_early")
    @Expose
    private String numLeaveEarly;
    @SerializedName("total_errors")
    @Expose
    private String totalErrors;
    @SerializedName("workdays")
    @Expose
    private ArrayList<Workday> listWorkdays;

    public StatisticalTimeKeeping() {
    }

    public StatisticalTimeKeeping(int idStatistical, User user, String numDayOff, String numWorkday, String numLateDay, String numLeaveEarly, String totalErrors, ArrayList<Workday> listWorkdays) {
        this.idStatistical = idStatistical;
        this.user = user;
        this.numDayOff = numDayOff;
        this.numWorkday = numWorkday;
        this.numLateDay = numLateDay;
        this.numLeaveEarly = numLeaveEarly;
        this.totalErrors = totalErrors;
        this.listWorkdays = listWorkdays;
    }

    public int getIdStatistical() {
        return idStatistical;
    }

    public void setIdStatistical(int idStatistical) {
        this.idStatistical = idStatistical;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNumDayOff() {
        return numDayOff;
    }

    public void setNumDayOff(String numDayOff) {
        this.numDayOff = numDayOff;
    }

    public String getNumWorkday() {
        return numWorkday;
    }

    public void setNumWorkday(String numWorkday) {
        this.numWorkday = numWorkday;
    }

    public String getNumLateDay() {
        return numLateDay;
    }

    public void setNumLateDay(String numLateDay) {
        this.numLateDay = numLateDay;
    }

    public String getNumLeaveEarly() {
        return numLeaveEarly;
    }

    public void setNumLeaveEarly(String numLeaveEarly) {
        this.numLeaveEarly = numLeaveEarly;
    }

    public String getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(String totalErrors) {
        this.totalErrors = totalErrors;
    }

    public ArrayList<Workday> getListWorkdays() {
        return listWorkdays;
    }

    public void setListWorkdays(ArrayList<Workday> listWorkdays) {
        this.listWorkdays = listWorkdays;
    }
}
