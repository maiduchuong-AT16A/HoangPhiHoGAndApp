package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Setting {
    @SerializedName("id_setting")
    @Expose
    private int idSetting;
    @SerializedName("time_start")
    @Expose
    private String timeStart;
    @SerializedName("time_end")
    @Expose
    private String timeEnd;
    @SerializedName("overtime")
    @Expose
    private double overtime;
    @SerializedName("holiday")
    @Expose
    private double holiday;
    @SerializedName("day_off")
    @Expose
    private double dayOff;

    public Setting() {
    }

    public Setting(int idSetting, String timeStart, String timeEnd, double overtime, double holiday, double dayOff) {
        this.idSetting = idSetting;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.overtime = overtime;
        this.holiday = holiday;
        this.dayOff = dayOff;
    }

    public int getIdSetting() {
        return idSetting;
    }

    public void setIdSetting(int idSetting) {
        this.idSetting = idSetting;
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

    public double getOvertime() {
        return overtime;
    }

    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }

    public double getHoliday() {
        return holiday;
    }

    public void setHoliday(double holiday) {
        this.holiday = holiday;
    }

    public double getDayOff() {
        return dayOff;
    }

    public void setDayOff(double dayOff) {
        this.dayOff = dayOff;
    }
}
