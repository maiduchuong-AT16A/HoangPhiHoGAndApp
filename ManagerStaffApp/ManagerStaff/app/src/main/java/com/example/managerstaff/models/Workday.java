package com.example.managerstaff.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Workday {

    @SerializedName("id_workday")
    @Expose
    private int idWorkday;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("name_day")
    @Expose
    private String nameDay;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("wage_sum")
    @Expose
    private double wageSum;
    @SerializedName("num_hour_work")
    @Expose
    private int numHourWork;
    @SerializedName("check_ins")
    @Expose
    private List<CheckIn> listCheckIns;
    @SerializedName("check_outs")
    @Expose
    private List<CheckOut> listCheckOuts;
    @SerializedName("calendars")
    @Expose
    private List<CalendarA> listCalendars;

    public Workday() {
    }

    public Workday(int idWorkday, String day, String status, double wageSum, int numHourWork, List<CheckIn> listCheckIns, List<CheckOut> listCheckOuts, List<CalendarA> listCalendars) {
        this.idWorkday = idWorkday;
        this.day = day;
        this.status = status;
        this.wageSum = wageSum;
        this.numHourWork = numHourWork;
        this.listCheckIns = listCheckIns;
        this.listCheckOuts = listCheckOuts;
        this.listCalendars = listCalendars;
    }

    public Workday(int idWorkday, String day, String nameDay, String status, double wageSum, int numHourWork, List<CheckIn> listCheckIns, List<CheckOut> listCheckOuts, List<CalendarA> listCalendars) {
        this.idWorkday = idWorkday;
        this.day = day;
        this.nameDay = nameDay;
        this.status = status;
        this.wageSum = wageSum;
        this.numHourWork = numHourWork;
        this.listCheckIns = listCheckIns;
        this.listCheckOuts = listCheckOuts;
        this.listCalendars = listCalendars;
    }

    public int getIdWorkday() {
        return idWorkday;
    }

    public void setIdWorkday(int idWorkday) {
        this.idWorkday = idWorkday;
    }

    public String getNameDay() {
        return nameDay;
    }

    public void setNameDay(String nameDay) {
        this.nameDay = nameDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getWageSum() {
        return wageSum;
    }

    public void setWageSum(double wageSum) {
        this.wageSum = wageSum;
    }

    public int getNumHourWork() {
        return numHourWork;
    }

    public void setNumHourWork(int numHourWork) {
        this.numHourWork = numHourWork;
    }

    public List<CheckIn> getListCheckIns() {
        return listCheckIns;
    }

    public void setListCheckIns(List<CheckIn> listCheckIns) {
        this.listCheckIns = listCheckIns;
    }

    public List<CheckOut> getListCheckOuts() {
        return listCheckOuts;
    }

    public void setListCheckOuts(List<CheckOut> listCheckOuts) {
        this.listCheckOuts = listCheckOuts;
    }

    public List<CalendarA> getListCalendars() {
        return listCalendars;
    }

    public void setListCalendars(List<CalendarA> listCalendars) {
        this.listCalendars = listCalendars;
    }
}
